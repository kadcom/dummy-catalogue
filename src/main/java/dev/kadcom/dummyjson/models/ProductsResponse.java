package dev.kadcom.dummyjson.models;

import java.util.List;

public class ProductsResponse {
    private List<Product> products;
    private int total;
    private int skip;
    private int limit;
    
    public List<Product> getProducts() { return products; }
    public int getTotal() { return total; }
    public int getSkip() { return skip; }
    public int getLimit() { return limit; }
    
    public boolean hasMore() {
        return skip + limit < total;
    }
    
    public int getCurrentPage() {
        return limit > 0 ? (skip / limit) + 1 : 1;
    }
    
    public int getTotalPages() {
        return limit > 0 ? (int) Math.ceil((double) total / limit) : 1;
    }
    
    @Override
    public String toString() {
        return "ProductsResponse{" +
                "total=" + total +
                ", skip=" + skip +
                ", limit=" + limit +
                ", productsCount=" + (products != null ? products.size() : 0) +
                '}';
    }
}