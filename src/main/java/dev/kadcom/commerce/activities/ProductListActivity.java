package dev.kadcom.commerce.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
// SwipeRefreshLayout removed to reduce APK size
import dev.kadcom.commerce.adapters.ProductAdapter;
import dev.kadcom.commerce.models.ProductItem;
import dev.kadcom.commerce.utils.StyleUtils;
import dev.kadcom.commerce.views.ProductCardView;
import dev.kadcom.dummyjson.client.DummyJsonClient;
import dev.kadcom.dummyjson.models.Product;
import dev.kadcom.dummyjson.models.ProductsResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Main product list activity with RecyclerView grid
 * Pure Java implementation with programmatic UI
 */
public class ProductListActivity extends Activity {
    
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    // SwipeRefreshLayout removed for smaller APK
    private ProgressBar loadingProgress;
    private DummyJsonClient apiClient;
    
    // Pagination
    private static final int PAGE_SIZE = 20;
    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasMoreData = true;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setupApiClient();
        createViews();
        setupRecyclerView();
        // setupSwipeRefresh(); // Removed for smaller APK
        
        loadInitialData();
    }
    
    private void setupApiClient() {
        // Use the DummyJSON client library
        apiClient = DummyJsonClient.withOkHttp();
    }
    
    private void createViews() {
        // Create main container
        ViewGroup container = createMainContainer();
        
        // Create loading progress bar
        loadingProgress = createLoadingProgress();
        
        // Create swipe refresh layout
        // swipeRefresh = createSwipeRefreshLayout(); // Removed for smaller APK
        
        // Create RecyclerView
        recyclerView = createRecyclerView();
        
        // Add views to container (SwipeRefreshLayout removed for smaller APK)
        container.addView(recyclerView);
        container.addView(loadingProgress);
        
        setContentView(container);
    }
    
    private ViewGroup createMainContainer() {
        android.widget.FrameLayout container = new android.widget.FrameLayout(this);
        container.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        container.setBackgroundColor(StyleUtils.BACKGROUND_COLOR);
        return container;
    }
    
    private ProgressBar createLoadingProgress() {
        ProgressBar progress = new ProgressBar(this);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = android.view.Gravity.CENTER;
        progress.setLayoutParams(params);
        progress.setVisibility(View.VISIBLE);
        return progress;
    }
    
    // SwipeRefreshLayout removed to reduce APK size
    // private SwipeRefreshLayout createSwipeRefreshLayout() {
    //     SwipeRefreshLayout swipeRefresh = new SwipeRefreshLayout(this);
    //     swipeRefresh.setLayoutParams(new ViewGroup.LayoutParams(
    //         ViewGroup.LayoutParams.MATCH_PARENT,
    //         ViewGroup.LayoutParams.MATCH_PARENT
    //     ));
    //     
    //     // Style the refresh indicator
    //     swipeRefresh.setColorSchemeColors(
    //         StyleUtils.PRIMARY_COLOR,
    //         StyleUtils.ACCENT_COLOR
    //     );
    //     
    //     return swipeRefresh;
    // }
    
    private RecyclerView createRecyclerView() {
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        
        // Add padding for better visual spacing
        int padding = StyleUtils.dpToPx(this, StyleUtils.SPACING_SMALL);
        recyclerView.setPadding(padding, padding, padding, padding);
        recyclerView.setClipToPadding(false);
        
        return recyclerView;
    }
    
    private void setupRecyclerView() {
        // Calculate number of columns based on screen width
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int cardWidth = StyleUtils.dpToPx(this, 160); // Minimum card width
        int spanCount = Math.max(2, screenWidth / cardWidth);
        
        // Setup grid layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(layoutManager);
        
        // Create and set adapter
        adapter = new ProductAdapter();
        adapter.setOnProductClickListener(this::onProductClick);
        recyclerView.setAdapter(adapter);
        
        // Add scroll listener for infinite scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                if (!isLoading && hasMoreData && dy > 0) {
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int totalItems = layoutManager.getItemCount();
                        int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                        
                        if (lastVisible >= totalItems - 5) { // Load more when 5 items from end
                            loadMoreProducts();
                        }
                    }
                }
            }
        });
        
        // Optimize RecyclerView performance
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }
    
    private void setupSwipeRefresh() {
        // SwipeRefreshLayout removed for smaller APK
    }
    
    private void loadInitialData() {
        loadProducts(false);
    }
    
    private void loadProducts(boolean isRefresh) {
        if (isLoading) return;
        
        isLoading = true;
        
        if (!isRefresh) {
            loadingProgress.setVisibility(View.VISIBLE);
        }
        
        // Calculate skip parameter for pagination
        int skip = currentPage * PAGE_SIZE;
        
        // Use DummyJSON client to fetch products
        CompletableFuture<ProductsResponse> future = apiClient.getProductsAsync();
        
        future.thenAccept(response -> {
            runOnUiThread(() -> {
                isLoading = false;
                loadingProgress.setVisibility(View.GONE);
                // swipeRefresh.setRefreshing(false); // Removed for smaller APK
                
                if (response != null && response.getProducts() != null) {
                    List<ProductItem> productItems = response.getProducts().stream()
                        .map(ProductItem::new)
                        .collect(Collectors.toList());
                    
                    // For now, just update all products (DummyJSON returns all at once)
                    adapter.updateProducts(productItems);
                    hasMoreData = false; // No pagination support yet
                } else {
                    showError("Failed to load products");
                }
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> {
                isLoading = false;
                loadingProgress.setVisibility(View.GONE);
                // swipeRefresh.setRefreshing(false); // Removed for smaller APK
                showError("Network error: " + throwable.getMessage());
            });
            return null;
        });
    }
    
    private void loadMoreProducts() {
        loadProducts(false);
    }
    
    private void onProductClick(ProductItem product) {
        // Navigate to product detail activity
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product_id", product.getId());
        intent.putExtra("product_title", product.getTitle());
        startActivity(intent);
        
        // Add transition animation
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data if needed
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear image cache to free memory
        dev.kadcom.commerce.utils.ImageLoader.getInstance().clearCache();
    }
}