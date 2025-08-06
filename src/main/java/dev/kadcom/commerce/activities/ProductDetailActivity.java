package dev.kadcom.commerce.activities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
// ViewPager2 removed to reduce APK size
import dev.kadcom.commerce.models.ProductItem;
import dev.kadcom.commerce.utils.StyleUtils;
import dev.kadcom.commerce.views.AsyncImageView;
import dev.kadcom.dummyjson.client.DummyJsonClient;
import dev.kadcom.dummyjson.models.Product;
import java.util.concurrent.CompletableFuture;

/**
 * Product detail activity with custom image gallery and detailed information
 * Pure Java implementation with Canvas-based image rendering
 */
public class ProductDetailActivity extends Activity {
    
    private DummyJsonClient apiClient;
    private ProgressBar loadingProgress;
    private ScrollView scrollView;
    private LinearLayout contentContainer;
    
    // Product data
    private int productId;
    private ProductItem currentProduct;
    
    // UI Components
    // ViewPager2 removed for smaller APK
    private TextView titleView;
    private TextView brandView;
    private TextView priceView;
    private TextView originalPriceView;
    private TextView discountView;
    private TextView ratingView;
    private TextView stockView;
    private TextView descriptionView;
    private TextView categoryView;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get product ID from intent
        productId = getIntent().getIntExtra("product_id", -1);
        if (productId == -1) {
            finish();
            return;
        }
        
        setupApiClient();
        createViews();
        loadProductDetails();
    }
    
    private void setupApiClient() {
        apiClient = DummyJsonClient.withOkHttp();
    }
    
    private void createViews() {
        // Main container
        LinearLayout mainContainer = new LinearLayout(this);
        mainContainer.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setBackgroundColor(StyleUtils.SURFACE_COLOR);
        
        // Loading progress
        loadingProgress = createLoadingProgress();
        
        // Scroll view for content
        scrollView = createScrollView();
        contentContainer = createContentContainer();
        scrollView.addView(contentContainer);
        
        // Add views to main container
        android.widget.FrameLayout frameLayout = new android.widget.FrameLayout(this);
        frameLayout.addView(scrollView);
        frameLayout.addView(loadingProgress);
        
        setContentView(frameLayout);
        
        // Initially hide content
        scrollView.setVisibility(View.GONE);
    }
    
    private ProgressBar createLoadingProgress() {
        ProgressBar progress = new ProgressBar(this);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        progress.setLayoutParams(params);
        return progress;
    }
    
    private ScrollView createScrollView() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        scrollView.setFillViewport(true);
        return scrollView;
    }
    
    private LinearLayout createContentContainer() {
        LinearLayout container = new LinearLayout(this);
        container.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        container.setOrientation(LinearLayout.VERTICAL);
        
        // Add padding
        int padding = StyleUtils.dpToPx(this, StyleUtils.SPACING_MEDIUM);
        container.setPadding(padding, padding, padding, padding);
        
        createDetailViews(container);
        
        return container;
    }
    
    private void createDetailViews(LinearLayout container) {
        // Image gallery section
        createImageGallery(container);
        
        // Product info section
        createProductInfoSection(container);
        
        // Description section
        createDescriptionSection(container);
        
        // Add action buttons (could add cart, wishlist, etc.)
        createActionButtons(container);
    }
    
    private void createImageGallery(LinearLayout container) {
        // For now, create a single large image view
        // In a full implementation, this would be a ViewPager2 with image adapter
        AsyncImageView mainImage = new AsyncImageView(this);
        
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            StyleUtils.dpToPx(this, 300)
        );
        imageParams.bottomMargin = StyleUtils.dpToPx(this, StyleUtils.SPACING_LARGE);
        
        container.addView(mainImage, imageParams);
        
        // Store reference for binding
        // imageGallery = new ViewPager2(this); // Removed for smaller APK
        // imageGallery.setTag(mainImage); // Removed for smaller APK
    }
    
    private void createProductInfoSection(LinearLayout container) {
        // Brand
        brandView = createStyledTextView(StyleUtils.Typography.BODY_SIZE, StyleUtils.TEXT_SECONDARY);
        addViewWithMargin(container, brandView, StyleUtils.SPACING_SMALL);
        
        // Title
        titleView = createStyledTextView(StyleUtils.Typography.HEADLINE_SIZE, StyleUtils.TEXT_PRIMARY);
        titleView.setTypeface(StyleUtils.Typography.getBoldTypeface());
        addViewWithMargin(container, titleView, StyleUtils.SPACING_MEDIUM);
        
        // Price section
        LinearLayout priceContainer = createPriceSection();
        addViewWithMargin(container, priceContainer, StyleUtils.SPACING_MEDIUM);
        
        // Rating and stock
        LinearLayout metaContainer = createMetaSection();
        addViewWithMargin(container, metaContainer, StyleUtils.SPACING_MEDIUM);
        
        // Category
        categoryView = createStyledTextView(StyleUtils.Typography.BODY_SIZE, StyleUtils.ACCENT_COLOR);
        addViewWithMargin(container, categoryView, StyleUtils.SPACING_SMALL);
    }
    
    private LinearLayout createPriceSection() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setGravity(Gravity.CENTER_VERTICAL);
        
        // Current price
        priceView = createStyledTextView(StyleUtils.Typography.HEADLINE_SIZE, StyleUtils.PRIMARY_COLOR);
        priceView.setTypeface(StyleUtils.Typography.getBoldTypeface());
        container.addView(priceView);
        
        // Original price (crossed out)
        originalPriceView = createStyledTextView(StyleUtils.Typography.TITLE_SIZE, StyleUtils.TEXT_SECONDARY);
        originalPriceView.setPaintFlags(originalPriceView.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        LinearLayout.LayoutParams originalParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        originalParams.leftMargin = StyleUtils.dpToPx(this, StyleUtils.SPACING_MEDIUM);
        container.addView(originalPriceView, originalParams);
        
        // Discount badge
        discountView = createDiscountBadge();
        LinearLayout.LayoutParams discountParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        discountParams.leftMargin = StyleUtils.dpToPx(this, StyleUtils.SPACING_MEDIUM);
        container.addView(discountView, discountParams);
        
        return container;
    }
    
    private TextView createDiscountBadge() {
        TextView badge = createStyledTextView(StyleUtils.Typography.CAPTION_SIZE, Color.WHITE);
        badge.setTypeface(StyleUtils.Typography.getBoldTypeface());
        badge.setBackground(StyleUtils.createCardBackground(StyleUtils.ACCENT_COLOR, 
            StyleUtils.dpToPx(this, StyleUtils.RADIUS_SMALL)));
        
        int padding = StyleUtils.dpToPx(this, StyleUtils.SPACING_SMALL);
        badge.setPadding(padding, padding/2, padding, padding/2);
        
        return badge;
    }
    
    private LinearLayout createMetaSection() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setGravity(Gravity.CENTER_VERTICAL);
        
        // Rating
        ratingView = createStyledTextView(StyleUtils.Typography.BODY_SIZE, StyleUtils.TEXT_SECONDARY);
        container.addView(ratingView);
        
        // Divider
        TextView divider = createStyledTextView(StyleUtils.Typography.BODY_SIZE, StyleUtils.DIVIDER_COLOR);
        divider.setText(" • ");
        container.addView(divider);
        
        // Stock status
        stockView = createStyledTextView(StyleUtils.Typography.BODY_SIZE, StyleUtils.PRIMARY_COLOR);
        stockView.setTypeface(StyleUtils.Typography.getBoldTypeface());
        container.addView(stockView);
        
        return container;
    }
    
    private void createDescriptionSection(LinearLayout container) {
        // Description title
        TextView descTitle = createStyledTextView(StyleUtils.Typography.TITLE_SIZE, StyleUtils.TEXT_PRIMARY);
        descTitle.setText("Description");
        descTitle.setTypeface(StyleUtils.Typography.getBoldTypeface());
        addViewWithMargin(container, descTitle, StyleUtils.SPACING_LARGE);
        
        // Description content
        descriptionView = createStyledTextView(StyleUtils.Typography.BODY_SIZE, StyleUtils.TEXT_PRIMARY);
        descriptionView.setLineSpacing(StyleUtils.dpToPx(this, 4), 1.0f);
        addViewWithMargin(container, descriptionView, StyleUtils.SPACING_SMALL);
    }
    
    private void createActionButtons(LinearLayout container) {
        LinearLayout buttonContainer = new LinearLayout(this);
        buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonContainer.setGravity(Gravity.CENTER);
        
        // Add to Cart button (placeholder)
        TextView addToCartBtn = createActionButton("Add to Cart", StyleUtils.PRIMARY_COLOR);
        TextView buyNowBtn = createActionButton("Buy Now", StyleUtils.ACCENT_COLOR);
        
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f
        );
        btnParams.rightMargin = StyleUtils.dpToPx(this, StyleUtils.SPACING_SMALL);
        buttonContainer.addView(addToCartBtn, btnParams);
        
        btnParams = new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f
        );
        btnParams.leftMargin = StyleUtils.dpToPx(this, StyleUtils.SPACING_SMALL);
        buttonContainer.addView(buyNowBtn, btnParams);
        
        addViewWithMargin(container, buttonContainer, StyleUtils.SPACING_XL);
    }
    
    private TextView createActionButton(String text, int backgroundColor) {
        TextView button = new TextView(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(StyleUtils.Typography.SUBTITLE_SIZE);
        button.setTypeface(StyleUtils.Typography.getBoldTypeface());
        button.setGravity(Gravity.CENTER);
        
        int padding = StyleUtils.dpToPx(this, StyleUtils.SPACING_MEDIUM);
        button.setPadding(padding, padding, padding, padding);
        
        button.setBackground(StyleUtils.createCardBackground(backgroundColor, 
            StyleUtils.dpToPx(this, StyleUtils.RADIUS_MEDIUM)));
        
        // Add click effect
        button.setClickable(true);
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.8f);
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1.0f);
                    break;
            }
            return false;
        });
        
        return button;
    }
    
    private TextView createStyledTextView(int textSize, int textColor) {
        TextView textView = new TextView(this);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);
        return textView;
    }
    
    private void addViewWithMargin(LinearLayout container, View view, int bottomMarginDp) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = StyleUtils.dpToPx(this, bottomMarginDp);
        container.addView(view, params);
    }
    
    private void loadProductDetails() {
        CompletableFuture<Product> future = apiClient.getProductAsync(productId);
        
        future.thenAccept(product -> {
            runOnUiThread(() -> {
                if (product != null) {
                    currentProduct = new ProductItem(product);
                    bindProductData();
                    showContent();
                } else {
                    showError("Product not found");
                }
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> showError("Failed to load product: " + throwable.getMessage()));
            return null;
        });
    }
    
    private void bindProductData() {
        if (currentProduct == null) return;
        
        // Bind image
        // AsyncImageView mainImage = (AsyncImageView) imageGallery.getTag(); // Removed for smaller APK
        // if (mainImage != null) { // Removed for smaller APK
        //     mainImage.loadImage(currentProduct.getPrimaryImageUrl());
        // }
        
        // Bind text data
        brandView.setText(currentProduct.getBrand());
        titleView.setText(currentProduct.getTitle());
        categoryView.setText("Category: " + currentProduct.getCategory());
        ratingView.setText(currentProduct.getFormattedRating());
        stockView.setText(currentProduct.getStockStatus());
        descriptionView.setText(currentProduct.getDescription());
        
        // Handle pricing
        if (currentProduct.hasDiscount()) {
            priceView.setText(currentProduct.getFormattedDiscountedPrice());
            originalPriceView.setText(currentProduct.getFormattedPrice());
            originalPriceView.setVisibility(View.VISIBLE);
            discountView.setText(currentProduct.getFormattedDiscount());
            discountView.setVisibility(View.VISIBLE);
        } else {
            priceView.setText(currentProduct.getFormattedPrice());
            originalPriceView.setVisibility(View.GONE);
            discountView.setVisibility(View.GONE);
        }
        
        // Stock status color
        if (currentProduct.isInStock()) {
            stockView.setTextColor(StyleUtils.PRIMARY_COLOR);
        } else {
            stockView.setTextColor(StyleUtils.ACCENT_COLOR);
        }
    }
    
    private void showContent() {
        loadingProgress.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }
    
    private void showError(String message) {
        loadingProgress.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}