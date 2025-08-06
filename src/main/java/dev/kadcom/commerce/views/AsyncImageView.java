package dev.kadcom.commerce.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import dev.kadcom.commerce.utils.ImageLoader;
import dev.kadcom.commerce.utils.StyleUtils;

/**
 * High-performance custom image view that draws directly to Canvas
 * Eliminates ImageView overhead for buttery smooth scrolling
 */
public class AsyncImageView extends View {
    
    private static final String TAG = "AsyncImageView";
    
    private Bitmap bitmap;
    private Paint paint;
    private Paint placeholderPaint;
    private Paint loadingPaint;
    private RectF drawRect;
    private String currentUrl;
    private boolean isLoading;
    
    // Loading animation - subtle pulse effect
    private float loadingAlpha = 0.3f;
    private boolean loadingAlphaIncreasing = true;
    private Runnable loadingAnimator;
    
    // Placeholder styling
    private final int placeholderColor;
    private final int cornerRadius;
    
    public AsyncImageView(Context context) {
        super(context);
        
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        
        placeholderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        placeholderColor = StyleUtils.BACKGROUND_COLOR;
        placeholderPaint.setColor(placeholderColor);
        
        // Loading indicator paint - very subtle
        loadingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        loadingPaint.setColor(StyleUtils.PRIMARY_COLOR);
        loadingPaint.setStyle(Paint.Style.FILL);
        
        cornerRadius = StyleUtils.dpToPx(context, StyleUtils.RADIUS_MEDIUM);
        drawRect = new RectF();
        
        // Loading animation runnable - subtle pulse effect
        loadingAnimator = new Runnable() {
            @Override
            public void run() {
                if (isLoading) {
                    // Gentle alpha pulsing
                    if (loadingAlphaIncreasing) {
                        loadingAlpha += 0.02f;
                        if (loadingAlpha >= 0.8f) {
                            loadingAlpha = 0.8f;
                            loadingAlphaIncreasing = false;
                        }
                    } else {
                        loadingAlpha -= 0.02f;
                        if (loadingAlpha <= 0.3f) {
                            loadingAlpha = 0.3f;
                            loadingAlphaIncreasing = true;
                        }
                    }
                    invalidate();
                    postDelayed(this, 50); // Slower, more subtle animation
                }
            }
        };
        
        // Set default size for layout
        setMinimumWidth(StyleUtils.dpToPx(context, 120));
        setMinimumHeight(StyleUtils.dpToPx(context, 120));
    }
    
    /**
     * Load image from URL asynchronously
     */
    public void loadImage(String url) {
        if (url == null || url.isEmpty()) {
            clearImage();
            return;
        }
        
        // If same URL and already have bitmap, don't reload but ensure it's displayed
        if (url.equals(currentUrl) && bitmap != null && !bitmap.isRecycled()) {
            Log.d(TAG, "Same URL, bitmap exists: " + url.substring(Math.max(0, url.length() - 20)));
            // Still trigger a redraw to make sure image is visible
            invalidate();
            return;
        }
        
        // Update URL first
        currentUrl = url;
        
        // Start loading state (but don't clear existing bitmap immediately)
        isLoading = true;
        startLoadingAnimation();
        invalidate(); // Trigger redraw for loading state
        
        // Calculate target size based on view dimensions
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        
        if (width <= 0 || height <= 0) {
            // View not measured yet, use default size
            width = StyleUtils.dpToPx(getContext(), 300);
            height = StyleUtils.dpToPx(getContext(), 300);
        }
        
        ImageLoader.getInstance().loadImage(url, width, height, new ImageLoader.ImageLoadCallback() {
            @Override
            public void onSuccess(Bitmap loadedBitmap) {
                // Check if URL is still current (prevent outdated images in recycled views)
                if (url.equals(currentUrl)) {
                    Log.d(TAG, "Image success for: " + url.substring(Math.max(0, url.length() - 20)));
                    bitmap = loadedBitmap;
                    isLoading = false;
                    stopLoadingAnimation();
                    // Force immediate UI update
                    invalidate();
                } else {
                    Log.d(TAG, "Ignoring outdated image: " + url.substring(Math.max(0, url.length() - 20)));
                }
            }
            
            @Override
            public void onError(String error) {
                if (url.equals(currentUrl)) {
                    Log.w(TAG, "Image error for: " + url.substring(Math.max(0, url.length() - 20)) + " - " + error);
                    isLoading = false;
                    stopLoadingAnimation();
                    // Force immediate UI update 
                    invalidate();
                }
            }
        });
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();
        
        if (width <= 0 || height <= 0) {
            return;
        }
        
        drawRect.set(0, 0, width, height);
        
        if (bitmap != null && !bitmap.isRecycled()) {
            // Draw the loaded bitmap
            drawBitmapWithRoundedCorners(canvas, bitmap, drawRect);
            
            // Draw subtle loading indicator over image if still loading
            if (isLoading) {
                drawLoadingIndicator(canvas, drawRect);
            }
        } else {
            // Draw placeholder
            drawPlaceholder(canvas, drawRect);
        }
    }
    
    /**
     * Draw bitmap with rounded corners for card-like appearance
     */
    private void drawBitmapWithRoundedCorners(Canvas canvas, Bitmap bitmap, RectF destRect) {
        // Calculate source rect to maintain aspect ratio
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        
        float scaleX = destRect.width() / bitmapWidth;
        float scaleY = destRect.height() / bitmapHeight;
        float scale = Math.max(scaleX, scaleY); // Scale to fill (crop if needed)
        
        float scaledWidth = bitmapWidth * scale;
        float scaledHeight = bitmapHeight * scale;
        
        float dx = (destRect.width() - scaledWidth) * 0.5f;
        float dy = (destRect.height() - scaledHeight) * 0.5f;
        
        RectF sourceRect = new RectF(
            Math.max(0, -dx / scale),
            Math.max(0, -dy / scale),
            Math.min(bitmapWidth, (destRect.width() - dx) / scale),
            Math.min(bitmapHeight, (destRect.height() - dy) / scale)
        );
        
        // Save canvas state for clipping
        int saveCount = canvas.save();
        
        // Clip to rounded rectangle
        canvas.clipRect(destRect);
        
        // Draw bitmap
        canvas.drawBitmap(bitmap, 
            new Rect((int)sourceRect.left, (int)sourceRect.top, (int)sourceRect.right, (int)sourceRect.bottom),
            new Rect((int)destRect.left, (int)destRect.top, (int)destRect.right, (int)destRect.bottom),
            paint);
        
        // Restore canvas
        canvas.restoreToCount(saveCount);
        
        // Draw rounded corner overlay (optional, for stronger corner effect)
        drawRoundedCornerOverlay(canvas, destRect);
    }
    
    /**
     * Draw placeholder with subtle styling
     */
    private void drawPlaceholder(Canvas canvas, RectF rect) {
        // Draw background
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, placeholderPaint);
        
        // Draw loading indicator or icon
        if (isLoading) {
            drawLoadingIndicator(canvas, rect);
        } else {
            // Draw error state with icon
            Paint errorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            errorPaint.setColor(StyleUtils.TEXT_SECONDARY);
            errorPaint.setTextAlign(Paint.Align.CENTER);
            errorPaint.setTextSize(StyleUtils.spToPx(getContext(), StyleUtils.Typography.CAPTION_SIZE));
            
            canvas.drawText("🖼️", 
                rect.centerX(), 
                rect.centerY(), 
                errorPaint);
        }
    }
    
    /**
     * Draw loading indicator - spinning arc
     */
    private void drawLoadingIndicator(Canvas canvas, RectF rect) {
        // Draw spinning arc in center
        float centerX = rect.centerX();
        float centerY = rect.centerY();
        float radius = Math.min(rect.width(), rect.height()) * 0.15f;
        
        RectF arcRect = new RectF(
            centerX - radius, centerY - radius,
            centerX + radius, centerY + radius
        );
        
        loadingPaint.setStyle(Paint.Style.STROKE);
        loadingPaint.setStrokeWidth(StyleUtils.dpToPx(getContext(), 2));
        loadingPaint.setAlpha(180);
        
        // Calculate rotation angle based on current time
        long currentTime = System.currentTimeMillis();
        float angle = (currentTime / 10) % 360;
        
        // Draw arc
        canvas.drawArc(arcRect, angle, 90, false, loadingPaint);
    }
    
    /**
     * Start loading animation
     */
    private void startLoadingAnimation() {
        removeCallbacks(loadingAnimator);
        post(loadingAnimator);
    }
    
    /**
     * Stop loading animation
     */
    private void stopLoadingAnimation() {
        removeCallbacks(loadingAnimator);
        loadingAlpha = 0.3f;
        loadingAlphaIncreasing = true;
    }
    
    /**
     * Draw subtle overlay to enhance rounded corners
     */
    private void drawRoundedCornerOverlay(Canvas canvas, RectF rect) {
        // This could add a subtle border or shadow effect
        // For now, keeping it simple for performance
    }
    
    /**
     * Clear current image (useful for recycling)
     */
    public void clearImage() {
        stopLoadingAnimation();
        currentUrl = null;
        bitmap = null;
        isLoading = false;
        invalidate();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Stop animation and clear image to help with memory management
        stopLoadingAnimation();
        // Don't clear image here to maintain state during recycling
        // clearImage();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Default to square aspect ratio if not specified
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = width; // Make it square
        }
        
        setMeasuredDimension(width, height);
    }
    
    /**
     * Get current image URL for debugging
     */
    public String getCurrentUrl() {
        return currentUrl;
    }
    
    /**
     * Check if currently loading
     */
    public boolean isCurrentlyLoading() {
        return isLoading;
    }
}