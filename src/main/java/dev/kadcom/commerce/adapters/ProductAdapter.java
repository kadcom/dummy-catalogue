package dev.kadcom.commerce.adapters;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import dev.kadcom.commerce.models.ProductItem;
import dev.kadcom.commerce.utils.StyleUtils;
import dev.kadcom.commerce.views.ProductCardView;
import java.util.ArrayList;
import java.util.List;

/**
 * High-performance RecyclerView adapter for product grid
 * Optimized for smooth scrolling with minimal allocations
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    
    private final List<ProductItem> products;
    private ProductCardView.OnProductClickListener clickListener;
    
    public ProductAdapter() {
        this.products = new ArrayList<>();
        
        // Enable stable IDs for better performance
        setHasStableIds(true);
    }
    
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductCardView cardView = new ProductCardView(parent.getContext());
        
        // Set layout parameters for grid
        int margin = StyleUtils.dpToPx(parent.getContext(), StyleUtils.SPACING_SMALL);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(margin, margin, margin, margin);
        cardView.setLayoutParams(params);
        
        return new ProductViewHolder(cardView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductItem product = products.get(position);
        holder.bind(product, clickListener);
    }
    
    @Override
    public int getItemCount() {
        return products.size();
    }
    
    @Override
    public long getItemId(int position) {
        return products.get(position).getId();
    }
    
    /**
     * Update products list with animation support
     */
    public void updateProducts(List<ProductItem> newProducts) {
        products.clear();
        products.addAll(newProducts);
        notifyDataSetChanged();
        
        // For better performance, consider using DiffUtil for large datasets:
        // DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ProductDiffCallback(oldProducts, newProducts));
        // diffResult.dispatchUpdatesTo(this);
    }
    
    /**
     * Add products (for infinite scroll)
     */
    public void addProducts(List<ProductItem> newProducts) {
        int startPosition = products.size();
        products.addAll(newProducts);
        notifyItemRangeInserted(startPosition, newProducts.size());
    }
    
    /**
     * Clear all products
     */
    public void clearProducts() {
        int size = products.size();
        products.clear();
        notifyItemRangeRemoved(0, size);
    }
    
    /**
     * Get product at position
     */
    public ProductItem getProduct(int position) {
        if (position >= 0 && position < products.size()) {
            return products.get(position);
        }
        return null;
    }
    
    /**
     * Set click listener for all product cards
     */
    public void setOnProductClickListener(ProductCardView.OnProductClickListener listener) {
        this.clickListener = listener;
    }
    
    /**
     * ViewHolder for product cards
     */
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        
        private final ProductCardView cardView;
        
        public ProductViewHolder(@NonNull ProductCardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
        
        public void bind(ProductItem product, ProductCardView.OnProductClickListener clickListener) {
            // Important: Don't clear content immediately to avoid flickering
            // The bindProduct method will handle proper state management
            
            // Bind new product data (this will handle image loading properly)
            cardView.bindProduct(product);
            
            // Set click listener
            cardView.setOnProductClickListener(clickListener);
        }
    }
    
    /**
     * Optional: DiffUtil callback for efficient updates
     * Uncomment and use for better performance with large datasets
     */
    /*
    private static class ProductDiffCallback extends DiffUtil.Callback {
        
        private final List<ProductItem> oldProducts;
        private final List<ProductItem> newProducts;
        
        public ProductDiffCallback(List<ProductItem> oldProducts, List<ProductItem> newProducts) {
            this.oldProducts = oldProducts;
            this.newProducts = newProducts;
        }
        
        @Override
        public int getOldListSize() {
            return oldProducts.size();
        }
        
        @Override
        public int getNewListSize() {
            return newProducts.size();
        }
        
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldProducts.get(oldItemPosition).getId() == newProducts.get(newItemPosition).getId();
        }
        
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            ProductItem oldProduct = oldProducts.get(oldItemPosition);
            ProductItem newProduct = newProducts.get(newItemPosition);
            return oldProduct.equals(newProduct);
        }
    }
    */
}