package dev.kadcom.dummyjson.models;

import java.util.List;
import java.util.Optional;

public class Product {
    private int id;
    private String title;
    private String description;
    private String category;
    private double price;
    private double discountPercentage;
    private double rating;
    private int stock;
    private List<String> tags;
    private String brand;
    private String sku;
    private int weight;
    private Dimensions dimensions;
    private String warrantyInformation;
    private String shippingInformation;
    private String availabilityStatus;
    private List<Review> reviews;
    private String returnPolicy;
    private int minimumOrderQuantity;
    private List<String> images;
    private String thumbnail;
    
    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public double getDiscountPercentage() { return discountPercentage; }
    public double getRating() { return rating; }
    public int getStock() { return stock; }
    public List<String> getTags() { return tags; }
    public Optional<String> getBrand() { return Optional.ofNullable(brand); }
    public String getSku() { return sku; }
    public int getWeight() { return weight; }
    public Optional<Dimensions> getDimensions() { return Optional.ofNullable(dimensions); }
    public Optional<String> getWarrantyInformation() { return Optional.ofNullable(warrantyInformation); }
    public Optional<String> getShippingInformation() { return Optional.ofNullable(shippingInformation); }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public List<Review> getReviews() { return reviews; }
    public Optional<String> getReturnPolicy() { return Optional.ofNullable(returnPolicy); }
    public int getMinimumOrderQuantity() { return minimumOrderQuantity; }
    public List<String> getImages() { return images; }
    public Optional<String> getThumbnail() { return Optional.ofNullable(thumbnail); }
    
    // Helper methods
    public double getDiscountedPrice() {
        return price * (1 - discountPercentage / 100);
    }
    
    public boolean isInStock() {
        return stock > 0 && "In Stock".equals(availabilityStatus);
    }
    
    public boolean isOnSale() {
        return discountPercentage > 0;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }
}