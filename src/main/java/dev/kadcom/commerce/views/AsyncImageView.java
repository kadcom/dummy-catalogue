package dev.kadcom.commerce.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import dev.kadcom.commerce.utils.ImageLoader;
import dev.kadcom.commerce.utils.StyleUtils;

/**
 * High-performance custom image view that draws directly to Canvas
 * Eliminates ImageView overhead for buttery smooth scrolling
 */
public class AsyncImageView extends View {
    
    private Bitmap bitmap;
    private Paint paint;
    private Paint placeholderPaint;
    private RectF drawRect;
    private String currentUrl;
    private boolean isLoading;
    
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
        
        cornerRadius = StyleUtils.dpToPx(context, StyleUtils.RADIUS_MEDIUM);
        drawRect = new RectF();
        
        // Set default size for layout
        setMinimumWidth(StyleUtils.dpToPx(context, 120));
        setMinimumHeight(StyleUtils.dpToPx(context, 120));
    }
    
    /**
     * Load image from URL asynchronously
     */
    public void loadImage(String url) {
        if (url == null || url.equals(currentUrl)) {
            return;
        }
        
        currentUrl = url;
        bitmap = null;
        isLoading = true;
        invalidate(); // Trigger redraw for placeholder
        
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
                    bitmap = loadedBitmap;
                    isLoading = false;
                    post(() -> invalidate()); // Update UI on main thread
                }
            }
            
            @Override
            public void onError(String error) {
                if (url.equals(currentUrl)) {
                    isLoading = false;
                    post(() -> invalidate()); // Show error state
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
        
        // Draw loading indicator or icon (simple approach)
        if (isLoading) {
            // Draw simple loading animation
            Paint loadingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            loadingPaint.setColor(StyleUtils.TEXT_SECONDARY);
            loadingPaint.setTextAlign(Paint.Align.CENTER);
            loadingPaint.setTextSize(StyleUtils.spToPx(getContext(), StyleUtils.Typography.CAPTION_SIZE));
            
            canvas.drawText("Loading...", 
                rect.centerX(), 
                rect.centerY(), 
                loadingPaint);
        } else {
            // Draw error state
            Paint errorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            errorPaint.setColor(StyleUtils.TEXT_SECONDARY);
            errorPaint.setTextAlign(Paint.Align.CENTER);
            errorPaint.setTextSize(StyleUtils.spToPx(getContext(), StyleUtils.Typography.CAPTION_SIZE));
            
            canvas.drawText("Image", 
                rect.centerX(), 
                rect.centerY(), 
                errorPaint);
        }
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
        currentUrl = null;
        bitmap = null;
        isLoading = false;
        invalidate();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Clear image to help with memory management
        clearImage();
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
}