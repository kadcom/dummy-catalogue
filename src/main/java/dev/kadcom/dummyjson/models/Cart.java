package dev.kadcom.dummyjson.models;

import java.util.List;

public class Cart {
    private int id;
    private List<CartProduct> products;
    private double total;
    private double discountedTotal;
    private int userId;
    private int totalProducts;
    private int totalQuantity;
    
    public int getId() { return id; }
    public List<CartProduct> getProducts() { return products; }
    public double getTotal() { return total; }
    public double getDiscountedTotal() { return discountedTotal; }
    public int getUserId() { return userId; }
    public int getTotalProducts() { return totalProducts; }
    public int getTotalQuantity() { return totalQuantity; }
    
    public double getTotalSavings() {
        return total - discountedTotal;
    }
    
    public double getSavingsPercentage() {
        return total > 0 ? (getTotalSavings() / total) * 100 : 0;
    }
    
    public boolean isEmpty() {
        return products == null || products.isEmpty();
    }
    
    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", totalProducts=" + totalProducts +
                ", totalQuantity=" + totalQuantity +
                ", total=" + total +
                ", discountedTotal=" + discountedTotal +
                '}';
    }
}