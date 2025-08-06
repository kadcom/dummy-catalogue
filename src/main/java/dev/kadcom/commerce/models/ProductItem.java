package dev.kadcom.commerce.models;

import dev.kadcom.dummyjson.models.Product;

/**
 * UI model wrapper for Product data
 * Provides additional UI-specific functionality while maintaining immutability
 */
public class ProductItem {
    
    private final Product product;
    private final String formattedPrice;
    private final String formattedDiscount;
    private final boolean hasDiscount;
    
    public ProductItem(Product product) {
        this.product = product;
        this.hasDiscount = product.getDiscountPercentage() > 0;
        
        // Format price with currency
        this.formattedPrice = String.format("$%.2f", product.getPrice());
        
        // Format discount if available
        if (hasDiscount) {
            this.formattedDiscount = String.format("-%.0f%%", product.getDiscountPercentage());
        } else {
            this.formattedDiscount = null;
        }
    }
    
    // Getters that delegate to the wrapped product
    public int getId() {
        return product.getId();
    }
    
    public String getTitle() {
        return product.getTitle();
    }
    
    public String getDescription() {
        return product.getDescription();
    }
    
    public String getCategory() {
        return product.getCategory();
    }
    
    public double getPrice() {
        return product.getPrice();
    }
    
    public double getDiscountPercentage() {
        return product.getDiscountPercentage();
    }
    
    public double getRating() {
        return product.getRating();
    }
    
    public int getStock() {
        return product.getStock();
    }
    
    public String getBrand() {
        return product.getBrand().orElse("Unknown");
    }
    
    public String getThumbnail() {
        return product.getThumbnail().orElse("");
    }
    
    public String[] getImages() {
        return product.getImages().toArray(new String[0]);
    }
    
    // UI-specific getters
    public String getFormattedPrice() {
        return formattedPrice;
    }
    
    public String getFormattedDiscount() {
        return formattedDiscount;
    }
    
    public boolean hasDiscount() {
        return hasDiscount;
    }
    
    public String getFormattedRating() {
        return String.format("%.1f★", product.getRating());
    }
    
    public String getStockStatus() {
        if (product.getStock() > 10) {
            return "In Stock";
        } else if (product.getStock() > 0) {
            return "Low Stock";
        } else {
            return "Out of Stock";
        }
    }
    
    public boolean isInStock() {
        return product.getStock() > 0;
    }
    
    /**
     * Get the primary image URL for display
     */
    public String getPrimaryImageUrl() {
        String[] images = getImages();
        if (images != null && images.length > 0) {
            return images[0];
        }
        return getThumbnail();
    }
    
    /**
     * Get discounted price if applicable
     */
    public String getFormattedDiscountedPrice() {
        if (hasDiscount) {
            double discountedPrice = product.getDiscountedPrice();
            return String.format("$%.2f", discountedPrice);
        }
        return formattedPrice;
    }
    
    /**
     * Get short description for list view
     */
    public String getShortDescription() {
        String description = getDescription();
        if (description.length() > 100) {
            return description.substring(0, 97) + "...";
        }
        return description;
    }
    
    /**
     * Get wrapped product for detailed operations
     */
    public Product getProduct() {
        return product;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProductItem that = (ProductItem) obj;
        return product.getId() == that.product.getId();
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(product.getId());
    }
    
    @Override
    public String toString() {
        return "ProductItem{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", price=" + getFormattedPrice() +
                ", discount=" + getFormattedDiscount() +
                '}';
    }
}