package dev.kadcom.dummyjson.models;

import java.util.Optional;

public class CartProduct {
    private int id;
    private String title;
    private double price;
    private int quantity;
    private double total;
    private double discountPercentage;
    private double discountedTotal;
    private String thumbnail;
    
    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public double getTotal() { return total; }
    public double getDiscountPercentage() { return discountPercentage; }
    public double getDiscountedTotal() { return discountedTotal; }
    public Optional<String> getThumbnail() { return Optional.ofNullable(thumbnail); }
    
    public double getSavings() {
        return total - discountedTotal;
    }
    
    public double getUnitPrice() {
        return quantity > 0 ? price : 0;
    }
    
    public double getDiscountedUnitPrice() {
        return quantity > 0 ? discountedTotal / quantity : 0;
    }
    
    @Override
    public String toString() {
        return "CartProduct{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", discountedTotal=" + discountedTotal +
                '}';
    }
}