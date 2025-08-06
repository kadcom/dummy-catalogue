package dev.kadcom.commerce.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import dev.kadcom.commerce.models.ProductItem;
import dev.kadcom.commerce.utils.StyleUtils;

/**
 * Custom product card view with programmatic layout and styling
 * Optimized for RecyclerView performance with minimal allocations
 */
public class ProductCardView extends LinearLayout {
    
    private AsyncImageView imageView;
    private TextView titleView;
    private TextView priceView;
    private TextView originalPriceView;
    private TextView discountView;
    private TextView ratingView;
    private TextView brandView;
    private View discountBadge;
    
    private ProductItem currentProduct;
    
    public ProductCardView(Context context) {
        super(context);
        initializeViews();
        setupLayout();
        applyStyles();
    }
    
    private void initializeViews() {
        setOrientation(VERTICAL);
        
        // Create image view
        imageView = new AsyncImageView(getContext());
        
        // Create text views
        titleView = new TextView(getContext());
        priceView = new TextView(getContext());
        originalPriceView = new TextView(getContext());
        discountView = new TextView(getContext());
        ratingView = new TextView(getContext());
        brandView = new TextView(getContext());
        
        // Create discount badge
        discountBadge = new View(getContext());
    }
    
    private void setupLayout() {
        Context context = getContext();
        
        // Card container with padding
        int padding = StyleUtils.dpToPx(context, StyleUtils.SPACING_MEDIUM);
        setPadding(padding, padding, padding, padding);
        
        // Image layout (aspect ratio maintained by AsyncImageView)
        int imageHeight = StyleUtils.dpToPx(context, 180);
        LayoutParams imageParams = new LayoutParams(
            LayoutParams.MATCH_PARENT, 
            imageHeight
        );
        imageParams.bottomMargin = StyleUtils.dpToPx(context, StyleUtils.SPACING_SMALL);
        addView(imageView, imageParams);
        
        // Brand name
        LayoutParams brandParams = new LayoutParams(
            LayoutParams.MATCH_PARENT, 
            LayoutParams.WRAP_CONTENT
        );
        brandParams.bottomMargin = StyleUtils.dpToPx(context, StyleUtils.SPACING_TINY);
        addView(brandView, brandParams);
        
        // Product title
        LayoutParams titleParams = new LayoutParams(
            LayoutParams.MATCH_PARENT, 
            LayoutParams.WRAP_CONTENT
        );
        titleParams.bottomMargin = StyleUtils.dpToPx(context, StyleUtils.SPACING_SMALL);
        addView(titleView, titleParams);
        
        // Price container
        LinearLayout priceContainer = createPriceContainer();
        LayoutParams priceContainerParams = new LayoutParams(
            LayoutParams.MATCH_PARENT, 
            LayoutParams.WRAP_CONTENT
        );
        priceContainerParams.bottomMargin = StyleUtils.dpToPx(context, StyleUtils.SPACING_SMALL);
        addView(priceContainer, priceContainerParams);
        
        // Rating
        LayoutParams ratingParams = new LayoutParams(
            LayoutParams.MATCH_PARENT, 
            LayoutParams.WRAP_CONTENT
        );
        addView(ratingView, ratingParams);
    }
    
    private LinearLayout createPriceContainer() {
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(HORIZONTAL);
        container.setGravity(Gravity.CENTER_VERTICAL);
        
        // Current price
        LayoutParams priceParams = new LayoutParams(
            LayoutParams.WRAP_CONTENT, 
            LayoutParams.WRAP_CONTENT
        );
        container.addView(priceView, priceParams);
        
        // Original price (crossed out)
        LayoutParams originalPriceParams = new LayoutParams(
            LayoutParams.WRAP_CONTENT, 
            LayoutParams.WRAP_CONTENT
        );
        originalPriceParams.leftMargin = StyleUtils.dpToPx(getContext(), StyleUtils.SPACING_SMALL);
        container.addView(originalPriceView, originalPriceParams);
        
        // Discount percentage
        LayoutParams discountParams = new LayoutParams(
            LayoutParams.WRAP_CONTENT, 
            LayoutParams.WRAP_CONTENT
        );
        discountParams.leftMargin = StyleUtils.dpToPx(getContext(), StyleUtils.SPACING_SMALL);
        container.addView(discountView, discountParams);
        
        return container;
    }
    
    private void applyStyles() {
        Context context = getContext();
        
        // Card background with elevation effect
        setBackground(StyleUtils.createElevatedCard(context));
        
        // Brand styling
        brandView.setTextSize(StyleUtils.Typography.CAPTION_SIZE);
        brandView.setTextColor(StyleUtils.TEXT_SECONDARY);
        brandView.setTypeface(Typeface.DEFAULT);
        brandView.setMaxLines(1);
        brandView.setSingleLine(true);
        
        // Title styling
        titleView.setTextSize(StyleUtils.Typography.SUBTITLE_SIZE);
        titleView.setTextColor(StyleUtils.TEXT_PRIMARY);
        titleView.setTypeface(StyleUtils.Typography.getBoldTypeface());
        titleView.setMaxLines(2);
        
        // Price styling
        priceView.setTextSize(StyleUtils.Typography.TITLE_SIZE);
        priceView.setTextColor(StyleUtils.PRIMARY_COLOR);
        priceView.setTypeface(StyleUtils.Typography.getBoldTypeface());
        
        // Original price styling (crossed out)
        originalPriceView.setTextSize(StyleUtils.Typography.BODY_SIZE);
        originalPriceView.setTextColor(StyleUtils.TEXT_SECONDARY);
        originalPriceView.setPaintFlags(originalPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        
        // Discount styling
        discountView.setTextSize(StyleUtils.Typography.CAPTION_SIZE);
        discountView.setTextColor(StyleUtils.ACCENT_COLOR);
        discountView.setTypeface(StyleUtils.Typography.getBoldTypeface());
        
        // Rating styling
        ratingView.setTextSize(StyleUtils.Typography.CAPTION_SIZE);
        ratingView.setTextColor(StyleUtils.TEXT_SECONDARY);
        
        // Card elevation simulation (shadow effect could be added here)
        setElevation(StyleUtils.dpToPx(context, 2));
    }
    
    /**
     * Bind product data to views
     */
    public void bindProduct(ProductItem product) {
        this.currentProduct = product;
        
        if (product == null) {
            clearContent();
            return;
        }
        
        // Load product image
        imageView.loadImage(product.getPrimaryImageUrl());
        
        // Set text content
        brandView.setText(product.getBrand());
        titleView.setText(product.getTitle());
        ratingView.setText(product.getFormattedRating());
        
        // Handle pricing and discounts
        if (product.hasDiscount()) {
            priceView.setText(product.getFormattedDiscountedPrice());
            originalPriceView.setText(product.getFormattedPrice());
            originalPriceView.setVisibility(VISIBLE);
            discountView.setText(product.getFormattedDiscount());
            discountView.setVisibility(VISIBLE);
        } else {
            priceView.setText(product.getFormattedPrice());
            originalPriceView.setVisibility(GONE);
            discountView.setVisibility(GONE);
        }
        
        // Stock status indication (could add color coding)
        if (!product.isInStock()) {
            setAlpha(0.6f);
        } else {
            setAlpha(1.0f);
        }
    }
    
    /**
     * Clear all content (for view recycling)
     */
    public void clearContent() {
        currentProduct = null;
        imageView.clearImage();
        titleView.setText("");
        priceView.setText("");
        originalPriceView.setText("");
        originalPriceView.setVisibility(GONE);
        discountView.setText("");
        discountView.setVisibility(GONE);
        ratingView.setText("");
        brandView.setText("");
        setAlpha(1.0f);
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
    
    /**
     * Optimize for recycling - cancel image loading
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (currentProduct != null) {
            // Cancel any pending image load for this URL
            imageView.clearImage();
        }
    }
    
    /**
     * Measure performance - ensure consistent sizing
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        // Ensure consistent card heights in grid for better visual alignment
        int width = getMeasuredWidth();
        int minHeight = StyleUtils.dpToPx(getContext(), 280); // Minimum card height
        
        if (getMeasuredHeight() < minHeight) {
            setMeasuredDimension(width, minHeight);
        }
    }
}