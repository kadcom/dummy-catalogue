package dev.kadcom.commerce.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import dev.kadcom.commerce.models.ProductItem;
import dev.kadcom.commerce.utils.ImageLoader;
import dev.kadcom.commerce.utils.StyleUtils;

/**
 * High-performance product card with pure Canvas drawing
 * Eliminates view hierarchy overhead for maximum RecyclerView performance
 */
public class ProductCardView extends View {
    
    private ProductItem currentProduct;
    private Bitmap productImage;
    private boolean imageLoading;
    
    // Card drawing cache
    private Bitmap cachedCardBitmap;
    private Canvas cachedCardCanvas;
    private boolean cardCacheDirty = true;
    private String cachedProductId;
    
    // Paint objects for different elements
    private Paint backgroundPaint;
    private Paint shadowPaint;
    private Paint imagePaint;
    private Paint loadingPaint;
    private Paint dividerPaint;
    private Paint badgePaint;
    private TextPaint brandPaint;
    private TextPaint titlePaint;
    private TextPaint pricePaint;
    private TextPaint originalPricePaint;
    private TextPaint discountPaint;
    private TextPaint ratingPaint;
    private TextPaint badgeTextPaint;
    
    // Layout measurements
    private RectF cardRect;
    private RectF shadowRect;
    private RectF imageRect;
    private RectF badgeRect;
    private float textStartY;
    private float lineHeight;
    private float sectionSpacing;
    private float itemSpacing;
    
    // Cached text layouts
    private StaticLayout titleLayout;
    private String cachedTitle;
    
    // Loading animation
    private float loadingRotation = 0f;
    private Runnable loadingAnimator;
    private boolean isScrolling = false;
    
    // Card dimensions
    private final int cardPadding;
    private final int imageHeight;
    private final int cornerRadius;
    
    public ProductCardView(Context context) {
        super(context);
        
        cardPadding = StyleUtils.dpToPx(context, 16); // Larger padding
        imageHeight = StyleUtils.dpToPx(context, 200); // Taller image
        cornerRadius = StyleUtils.dpToPx(context, 12); // Larger radius
        sectionSpacing = StyleUtils.dpToPx(context, 16); // Space between sections
        itemSpacing = StyleUtils.dpToPx(context, 6); // Space between items
        
        initializePaints();
        setupRects();
        setupLoadingAnimation();
        
        // Set click handling
        setClickable(true);
        setFocusable(true);
    }
    
    private void initializePaints() {
        // Background paint for card
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(0xFFFFFFFF); // Pure white
        backgroundPaint.setStyle(Paint.Style.FILL);
        
        // Shadow paint for elevation effect
        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setColor(0x10000000); // Light shadow
        shadowPaint.setStyle(Paint.Style.FILL);
        
        // Image paint
        imagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        imagePaint.setFilterBitmap(true);
        imagePaint.setDither(true);
        
        // Loading indicator paint
        loadingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        loadingPaint.setColor(StyleUtils.PRIMARY_COLOR);
        loadingPaint.setStyle(Paint.Style.STROKE);
        loadingPaint.setStrokeWidth(StyleUtils.dpToPx(getContext(), 3));
        
        // Divider paint for subtle separation
        dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dividerPaint.setColor(0xFFE0E0E0);
        dividerPaint.setStrokeWidth(StyleUtils.dpToPx(getContext(), 1));
        
        // Badge paint for discount/special offers
        badgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        badgePaint.setColor(StyleUtils.ACCENT_COLOR);
        badgePaint.setStyle(Paint.Style.FILL);
        
        // Text paints with improved typography
        brandPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        brandPaint.setTextSize(StyleUtils.spToPx(getContext(), 11));
        brandPaint.setColor(0xFF9E9E9E); // Lighter gray
        brandPaint.setTypeface(Typeface.DEFAULT);
        brandPaint.setLetterSpacing(0.05f); // Slight letter spacing
        
        titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTextSize(StyleUtils.spToPx(getContext(), 15));
        titlePaint.setColor(0xFF212121); // Darker text
        titlePaint.setTypeface(StyleUtils.Typography.getBoldTypeface());
        titlePaint.setLetterSpacing(-0.01f);
        
        pricePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        pricePaint.setTextSize(StyleUtils.spToPx(getContext(), 18));
        pricePaint.setColor(0xFF2E7D32); // Rich green
        pricePaint.setTypeface(StyleUtils.Typography.getBoldTypeface());
        
        originalPricePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        originalPricePaint.setTextSize(StyleUtils.spToPx(getContext(), 14));
        originalPricePaint.setColor(0xFF757575);
        originalPricePaint.setStrikeThruText(true);
        
        discountPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        discountPaint.setTextSize(StyleUtils.spToPx(getContext(), 11));
        discountPaint.setColor(0xFFFF5722); // Orange-red
        discountPaint.setTypeface(StyleUtils.Typography.getBoldTypeface());
        
        ratingPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        ratingPaint.setTextSize(StyleUtils.spToPx(getContext(), 12));
        ratingPaint.setColor(0xFF757575);
        
        badgeTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        badgeTextPaint.setTextSize(StyleUtils.spToPx(getContext(), 10));
        badgeTextPaint.setColor(0xFFFFFFFF);
        badgeTextPaint.setTypeface(StyleUtils.Typography.getBoldTypeface());
        badgeTextPaint.setTextAlign(Paint.Align.CENTER);
    }
    
    private void setupRects() {
        cardRect = new RectF();
        shadowRect = new RectF();
        imageRect = new RectF();
        badgeRect = new RectF();
        lineHeight = StyleUtils.spToPx(getContext(), 16) * 1.4f; // Better line height
    }
    
    private void setupLoadingAnimation() {
        loadingAnimator = new Runnable() {
            @Override
            public void run() {
                if (imageLoading && getVisibility() == View.VISIBLE && !isScrolling) {
                    loadingRotation += 6f; // 6 degrees per frame
                    if (loadingRotation >= 360f) {
                        loadingRotation = 0f;
                    }
                    // Mark cache dirty only for loading animation, not full rebuild
                    if (cachedCardBitmap != null && !cachedCardBitmap.isRecycled()) {
                        // Just update the loading indicator region instead of full invalidate
                        invalidateLoadingRegion();
                    } else {
                        invalidate();
                    }
                    postDelayed(this, 32); // Reduced to 30fps for performance
                }
            }
        };
    }
    
    private void invalidateLoadingRegion() {
        // Only invalidate the image area to reduce overdraw
        if (imageRect != null) {
            invalidate((int)imageRect.left, (int)imageRect.top, 
                      (int)imageRect.right, (int)imageRect.bottom);
        } else {
            invalidate();
        }
    }
    
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (currentProduct == null) {
            return;
        }
        
        int width = getWidth();
        int height = getHeight();
        
        if (width <= 0 || height <= 0) {
            return;
        }
        
        // Check if we can use cached card
        String productId = String.valueOf(currentProduct.getId());
        if (!cardCacheDirty && productId.equals(cachedProductId) && 
            cachedCardBitmap != null && !cachedCardBitmap.isRecycled() &&
            cachedCardBitmap.getWidth() == width && cachedCardBitmap.getHeight() == height) {
            
            // SINGLE draw call - just draw the cached bitmap
            canvas.drawBitmap(cachedCardBitmap, 0, 0, null);
            return; // No additional drawing to prevent overdraw
        }
        
        // Need to redraw card - prepare cache
        prepareCachedCard(width, height);
        
        // Draw to cache first, then copy to screen (single screen draw)
        if (cachedCardCanvas != null) {
            drawCardContent(cachedCardCanvas, width, height);
            // Now draw the completed cache to screen in one operation
            canvas.drawBitmap(cachedCardBitmap, 0, 0, null);
        } else {
            // Fallback: draw directly if no cache available
            drawCardContent(canvas, width, height);
        }
        
        // Mark cache as clean
        cardCacheDirty = false;
        cachedProductId = productId;
    }
    
    private void prepareCachedCard(int width, int height) {
        // Create or recreate cached bitmap if needed
        if (cachedCardBitmap == null || cachedCardBitmap.isRecycled() ||
            cachedCardBitmap.getWidth() != width || cachedCardBitmap.getHeight() != height) {
            
            if (cachedCardBitmap != null && !cachedCardBitmap.isRecycled()) {
                cachedCardBitmap.recycle();
            }
            
            try {
                cachedCardBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                cachedCardCanvas = new Canvas(cachedCardBitmap);
            } catch (OutOfMemoryError e) {
                // If we can't create cache, continue without it
                cachedCardBitmap = null;
                cachedCardCanvas = null;
            }
        } else if (cachedCardCanvas != null) {
            // Clear existing cache
            cachedCardCanvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
        }
    }
    
    private void drawCardContent(Canvas canvas, int width, int height) {
        // Update card rect with padding for shadow
        int shadowOffset = StyleUtils.dpToPx(getContext(), 2);
        cardRect.set(shadowOffset, shadowOffset, width - shadowOffset, height - shadowOffset);
        shadowRect.set(shadowOffset + 2, shadowOffset + 2, width, height);
        
        // Draw shadow first
        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint);
        
        // Draw card background with rounded corners
        canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, backgroundPaint);
        
        // Calculate layout
        calculateLayout(width, height);
        
        // Always draw image with loading state included in cache
        drawImageWithLoadingState(canvas);
        
        // Draw discount badge if applicable
        if (currentProduct.hasDiscount()) {
            drawDiscountBadge(canvas);
        }
        
        // Draw subtle divider between image and content
        drawContentDivider(canvas);
        
        // Draw text content with better spacing
        drawTextContent(canvas);
    }
    
    private void drawImageWithLoadingState(Canvas canvas) {
        if (productImage != null && !productImage.isRecycled()) {
            // Draw product image with proper scaling
            drawImageWithRoundedCorners(canvas, productImage, imageRect);
        } else {
            // Draw elegant placeholder
            drawImagePlaceholder(canvas, imageRect);
            
            // Include loading indicator in cached bitmap to prevent overdraw
            if (imageLoading) {
                drawLoadingIndicator(canvas, imageRect);
            }
        }
    }
    
    private void drawImagePlaceholder(Canvas canvas, RectF rect) {
        // Gradient placeholder background
        Paint placeholderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        placeholderPaint.setColor(0xFFF5F5F5); // Light gray
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, placeholderPaint);
        
        // Draw placeholder icon/text
        Paint iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        iconPaint.setColor(0xFFE0E0E0);
        iconPaint.setTextSize(StyleUtils.spToPx(getContext(), 24));
        iconPaint.setTextAlign(Paint.Align.CENTER);
        
        float centerX = rect.centerX();
        float centerY = rect.centerY();
        
        // Draw image icon
        canvas.drawText("📷", centerX, centerY + 8, iconPaint);
    }
    
    private void calculateLayout(int width, int height) {
        // Image rect (top portion of card)
        imageRect.set(
            cardPadding, 
            cardPadding,
            width - cardPadding, 
            cardPadding + imageHeight
        );
        
        // Text starts below image with some spacing
        textStartY = imageRect.bottom + StyleUtils.dpToPx(getContext(), StyleUtils.SPACING_SMALL);
        
        // Pre-calculate title layout if text changed
        String title = currentProduct.getTitle();
        if (!title.equals(cachedTitle)) {
            cachedTitle = title;
            int titleWidth = (int) (width - 2 * cardPadding);
            titleLayout = new StaticLayout(
                title,
                titlePaint,
                titleWidth,
                Layout.Alignment.ALIGN_NORMAL,
                1.0f,
                0f,
                false
            );
        }
    }
    
    
    private void drawDiscountBadge(Canvas canvas) {
        if (!currentProduct.hasDiscount()) return;
        
        String discountText = currentProduct.getFormattedDiscount();
        float badgeWidth = badgeTextPaint.measureText(discountText) + StyleUtils.dpToPx(getContext(), 12);
        float badgeHeight = StyleUtils.dpToPx(getContext(), 20);
        
        // Position badge in top-right corner of image
        badgeRect.set(
            imageRect.right - badgeWidth - StyleUtils.dpToPx(getContext(), 8),
            imageRect.top + StyleUtils.dpToPx(getContext(), 8),
            imageRect.right - StyleUtils.dpToPx(getContext(), 8),
            imageRect.top + StyleUtils.dpToPx(getContext(), 8) + badgeHeight
        );
        
        // Draw badge background with rounded corners
        canvas.drawRoundRect(badgeRect, badgeHeight / 2, badgeHeight / 2, badgePaint);
        
        // Draw badge text
        canvas.drawText(
            discountText, 
            badgeRect.centerX(), 
            badgeRect.centerY() + 4, 
            badgeTextPaint
        );
    }
    
    private void drawContentDivider(Canvas canvas) {
        float dividerY = imageRect.bottom + StyleUtils.dpToPx(getContext(), 8);
        float margin = StyleUtils.dpToPx(getContext(), 12);
        
        canvas.drawLine(
            cardRect.left + margin,
            dividerY,
            cardRect.right - margin,
            dividerY,
            dividerPaint
        );
    }
    
    private void drawImageWithRoundedCorners(Canvas canvas, Bitmap bitmap, RectF destRect) {
        // Calculate source rect to maintain aspect ratio (center crop)
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        
        float scaleX = destRect.width() / bitmapWidth;
        float scaleY = destRect.height() / bitmapHeight;
        float scale = Math.max(scaleX, scaleY);
        
        float scaledWidth = bitmapWidth * scale;
        float scaledHeight = bitmapHeight * scale;
        
        float dx = (destRect.width() - scaledWidth) * 0.5f;
        float dy = (destRect.height() - scaledHeight) * 0.5f;
        
        // Save canvas and clip to rounded rect
        int saveCount = canvas.save();
        canvas.clipRect(destRect);
        
        // Draw bitmap centered
        canvas.drawBitmap(bitmap,
            null,
            new RectF(
                destRect.left + dx,
                destRect.top + dy,
                destRect.left + dx + scaledWidth,
                destRect.top + dy + scaledHeight
            ),
            imagePaint);
        
        canvas.restoreToCount(saveCount);
    }
    
    private void drawLoadingIndicator(Canvas canvas, RectF rect) {
        float centerX = rect.centerX();
        float centerY = rect.centerY();
        float radius = Math.min(rect.width(), rect.height()) * 0.15f;
        
        RectF arcRect = new RectF(
            centerX - radius, centerY - radius,
            centerX + radius, centerY + radius
        );
        
        loadingPaint.setAlpha(180);
        canvas.drawArc(arcRect, loadingRotation, 90, false, loadingPaint);
    }
    
    private void drawTextContent(Canvas canvas) {
        float currentY = textStartY;
        float leftX = cardPadding;
        float rightX = getWidth() - cardPadding;
        
        // Draw brand name with better positioning
        String brand = currentProduct.getBrand();
        if (brand != null && !brand.isEmpty()) {
            canvas.drawText(brand.toUpperCase(), leftX, currentY, brandPaint);
            currentY += itemSpacing * 2; // More space after brand
        }
        
        // Draw title (multi-line support) with proper spacing
        if (titleLayout != null) {
            canvas.save();
            canvas.translate(leftX, currentY);
            titleLayout.draw(canvas);
            canvas.restore();
            currentY += titleLayout.getHeight() + sectionSpacing; // Much more space before price
        }
        
        // Visual separator removed per user request
        
        // Draw rating first (before price) with stars
        String rating = currentProduct.getFormattedRating();
        if (rating != null && !rating.isEmpty()) {
            drawRatingWithStars(canvas, leftX, currentY, rating);
            currentY += itemSpacing * 3; // More space after rating
        }
        
        // Add even more space before price section
        currentY += itemSpacing * 2;
        
        // Draw price information with better alignment
        drawPriceSection(canvas, leftX, rightX, currentY);
    }
    
    private void drawPriceSeparator(Canvas canvas, float leftX, float rightX, float y) {
        // Draw a very subtle separator line
        Paint separatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        separatorPaint.setColor(0xFFF0F0F0);
        separatorPaint.setStrokeWidth(1);
        
        float margin = StyleUtils.dpToPx(getContext(), 16);
        canvas.drawLine(leftX + margin, y, rightX - margin, y, separatorPaint);
    }
    
    private void drawRatingWithStars(Canvas canvas, float x, float y, String rating) {
        // Draw star emoji
        TextPaint starPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        starPaint.setTextSize(StyleUtils.spToPx(getContext(), 12));
        starPaint.setColor(0xFFFFB300); // Amber color
        
        canvas.drawText("⭐", x, y, starPaint);
        
        // Draw rating text next to star
        float starWidth = starPaint.measureText("⭐ ");
        canvas.drawText(rating, x + starWidth, y, ratingPaint);
    }
    
    private void drawPriceSection(Canvas canvas, float leftX, float rightX, float currentY) {
        if (currentProduct.hasDiscount()) {
            // Draw discounted price prominently
            String discountedPrice = currentProduct.getFormattedDiscountedPrice();
            canvas.drawText(discountedPrice, leftX, currentY, pricePaint);
            
            // Add more space between main price and original price/discount
            float nextLineY = currentY + (lineHeight * 1.2f); // Increased spacing
            float currentX = leftX;
            
            // Original price (crossed out)
            String originalPrice = currentProduct.getFormattedPrice();
            canvas.drawText(originalPrice, currentX, nextLineY, originalPricePaint);
            currentX += originalPricePaint.measureText(originalPrice) + StyleUtils.dpToPx(getContext(), 12); // More space
            
            // Discount percentage with better spacing
            String discount = currentProduct.getFormattedDiscount();
            canvas.drawText(discount, currentX, nextLineY, discountPaint);
        } else {
            // Draw regular price
            String price = currentProduct.getFormattedPrice();
            canvas.drawText(price, leftX, currentY, pricePaint);
        }
    }
    
    /**
     * Bind product data and load image
     */
    public void bindProduct(ProductItem product) {
        // Check if this is the same product to avoid unnecessary work
        if (this.currentProduct != null && product != null && 
            this.currentProduct.getId() == product.getId()) {
            return; // Same product, use cached drawing
        }
        
        this.currentProduct = product;
        cachedTitle = null; // Force title layout recalculation
        cardCacheDirty = true; // Mark cache as dirty for new product
        
        if (product == null) {
            clearContent();
            return;
        }
        
        // Load product image
        loadProductImage(product.getPrimaryImageUrl());
        
        // Trigger redraw
        invalidate();
    }
    
    private void loadProductImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            productImage = null;
            imageLoading = false;
            return;
        }
        
        imageLoading = true;
        startLoadingAnimation();
        
        // Calculate target size
        int targetWidth = (int) imageRect.width();
        int targetHeight = (int) imageRect.height();
        
        if (targetWidth <= 0 || targetHeight <= 0) {
            targetWidth = imageHeight;
            targetHeight = imageHeight;
        }
        
        ImageLoader.getInstance().loadImage(imageUrl, targetWidth, targetHeight, new ImageLoader.ImageLoadCallback() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                productImage = bitmap;
                imageLoading = false;
                stopLoadingAnimation();
                // Mark cache dirty since image changed
                cardCacheDirty = true;
                invalidate();
            }
            
            @Override
            public void onError(String error) {
                productImage = null;
                imageLoading = false;
                stopLoadingAnimation();
                // Mark cache dirty since image changed
                cardCacheDirty = true;
                invalidate();
            }
        });
    }
    
    private void startLoadingAnimation() {
        removeCallbacks(loadingAnimator);
        post(loadingAnimator);
    }
    
    private void stopLoadingAnimation() {
        removeCallbacks(loadingAnimator);
        loadingRotation = 0f;
    }
    
    /**
     * Clear all content (for view recycling)
     */
    public void clearContent() {
        currentProduct = null;
        cachedTitle = null;
        titleLayout = null;
        cardCacheDirty = true;
        cachedProductId = null;
        // Keep productImage and cached bitmap for potential reuse
        invalidate();
    }
    
    /**
     * Get currently bound product
     */
    public ProductItem getCurrentProduct() {
        return currentProduct;
    }
    
    /**
     * Handle click events (can be overridden or use listeners)
     */
    public void setOnProductClickListener(OnProductClickListener listener) {
        setOnClickListener(v -> {
            if (currentProduct != null && listener != null) {
                listener.onProductClick(currentProduct);
            }
        });
        
        // Add ripple effect programmatically
        setClickable(true);
        setFocusable(true);
        
        // Add subtle press effect
        setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    setAlpha(0.8f);
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    setAlpha(1.0f);
                    break;
            }
            return false; // Don't consume the event
        });
    }
    
    /**
     * Interface for product click handling
     */
    public interface OnProductClickListener {
        void onProductClick(ProductItem product);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Calculate desired height based on content with better spacing
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int shadowOffset = StyleUtils.dpToPx(getContext(), 2);
        int desiredHeight = shadowOffset + cardPadding * 2 + imageHeight + 
            StyleUtils.dpToPx(getContext(), 160); // Even more space for better spacing
        
        setMeasuredDimension(width, desiredHeight);
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLoadingAnimation();
        // Keep images for performance during recycling
        // Cache cleanup handled by RecyclerView pool
    }
    
    /**
     * Notify card about scroll state to optimize drawing
     */
    public void setScrolling(boolean scrolling) {
        this.isScrolling = scrolling;
        if (scrolling) {
            // Pause loading animation during scroll
            stopLoadingAnimation();
        } else if (imageLoading) {
            // Resume loading animation when scroll stops
            startLoadingAnimation();
        }
    }
    
    /**
     * Clean up resources (called when view is truly discarded)
     */
    public void cleanup() {
        if (cachedCardBitmap != null && !cachedCardBitmap.isRecycled()) {
            cachedCardBitmap.recycle();
            cachedCardBitmap = null;
        }
        cachedCardCanvas = null;
        productImage = null;
        stopLoadingAnimation();
    }
    
}