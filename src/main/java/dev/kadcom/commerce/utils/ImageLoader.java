package dev.kadcom.commerce.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

/**
 * High-performance image loader with LRU cache and OkHttp async loading
 * Optimized for smooth RecyclerView scrolling
 */
public class ImageLoader {
    
    private static final String TAG = "ImageLoader";
    private static ImageLoader instance;
    
    // Memory cache using LRU eviction
    private final LruCache<String, Bitmap> memoryCache;
    
    // Track ongoing downloads to prevent duplicates
    private final ConcurrentHashMap<String, Call> activeDownloads;
    
    // OkHttp client for async networking
    private final OkHttpClient httpClient;
    
    // Handler for UI thread callbacks
    private final Handler mainHandler;
    
    private ImageLoader() {
        // Calculate cache size (1/8 of available memory)
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024; // Size in KB
            }
        };
        
        activeDownloads = new ConcurrentHashMap<>();
        
        // Configure OkHttp for image loading
        httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .build();
            
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public static synchronized ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }
    
    /**
     * Load image asynchronously with callback using OkHttp
     */
    public void loadImage(String url, int targetWidth, int targetHeight, ImageLoadCallback callback) {
        if (url == null || url.isEmpty()) {
            mainHandler.post(() -> callback.onError("Invalid URL"));
            return;
        }
        
        // Check cache first
        Bitmap cached = getBitmapFromCache(url);
        if (cached != null && !cached.isRecycled()) {
            // Cache hit - return immediately
            Log.d(TAG, "Cache hit for: " + url.substring(Math.max(0, url.length() - 20)));
            callback.onSuccess(cached);
            return;
        }
        
        // Check if already downloading
        Call existingCall = activeDownloads.get(url);
        if (existingCall != null && !existingCall.isCanceled()) {
            // Request already in progress, could implement callback chaining here
            return;
        }
        
        // Start new OkHttp request
        Request request = new Request.Builder()
            .url(url)
            .build();
            
        Call call = httpClient.newCall(request);
        activeDownloads.put(url, call);
        
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activeDownloads.remove(url);
                mainHandler.post(() -> callback.onError("Network error: " + e.getMessage()));
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                activeDownloads.remove(url);
                
                if (!response.isSuccessful()) {
                    mainHandler.post(() -> callback.onError("HTTP error: " + response.code()));
                    response.close();
                    return;
                }
                
                try (ResponseBody body = response.body()) {
                    if (body == null) {
                        mainHandler.post(() -> callback.onError("Empty response body"));
                        return;
                    }
                    
                    // Read bytes to enable multiple passes
                    byte[] imageBytes = body.bytes();
                    
                    // Decode bitmap on background thread
                    Bitmap bitmap = decodeBitmap(imageBytes, targetWidth, targetHeight);
                    
                    if (bitmap != null) {
                        addBitmapToCache(url, bitmap);
                        Log.d(TAG, "Image loaded and cached: " + url.substring(Math.max(0, url.length() - 20)));
                        mainHandler.post(() -> callback.onSuccess(bitmap));
                    } else {
                        Log.w(TAG, "Failed to decode image: " + url.substring(Math.max(0, url.length() - 20)));
                        mainHandler.post(() -> callback.onError("Failed to decode image"));
                    }
                } catch (Exception e) {
                    mainHandler.post(() -> callback.onError("Decoding error: " + e.getMessage()));
                }
            }
        });
    }
    
    /**
     * Get bitmap from memory cache
     */
    private Bitmap getBitmapFromCache(String url) {
        return memoryCache.get(url);
    }
    
    /**
     * Add bitmap to memory cache
     */
    private void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null && bitmap != null) {
            memoryCache.put(url, bitmap);
        }
    }
    
    /**
     * Cancel pending request for URL
     */
    public void cancelRequest(String url) {
        Call call = activeDownloads.remove(url);
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }
    
    /**
     * Clear cache and cancel all pending requests
     */
    public void clearCache() {
        memoryCache.evictAll();
        
        // Cancel all active downloads
        for (Call call : activeDownloads.values()) {
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
        activeDownloads.clear();
    }
    
    /**
     * Callback interface for image loading
     */
    public interface ImageLoadCallback {
        void onSuccess(Bitmap bitmap);
        void onError(String error);
    }
    
    /**
     * Decode bitmap with efficient memory usage
     */
    private Bitmap decodeBitmap(byte[] imageBytes, int reqWidth, int reqHeight) {
        try {
            // First pass - get dimensions without loading full image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
            
            // Calculate sample size for memory efficiency
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565; // Use less memory
            
            // Second pass - decode with sampling
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
            
        } catch (Exception e) {
            Log.e(TAG, "Error decoding bitmap", e);
            return null;
        }
    }
    
    /**
     * Calculate optimal sample size to reduce memory usage
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        
        return inSampleSize;
    }
}