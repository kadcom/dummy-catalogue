package dev.kadcom.dummyjson.integration;

import dev.kadcom.dummyjson.client.DummyJsonClient;
import dev.kadcom.dummyjson.models.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class DummyJsonIntegrationTest {
    private DummyJsonClient client;
    
    @Before
    public void setUp() {
        client = DummyJsonClient.withOkHttp();
    }
    
    @Test
    public void testGetProducts() throws IOException {
        ProductsResponse response = client.getProducts();
        
        assertThat(response).isNotNull();
        assertThat(response.getProducts()).isNotEmpty();
        assertThat(response.getTotal()).isGreaterThan(0);
        assertThat(response.getLimit()).isEqualTo(30);
        assertThat(response.getSkip()).isEqualTo(0);
        
        Product firstProduct = response.getProducts().get(0);
        assertThat(firstProduct.getId()).isGreaterThan(0);
        assertThat(firstProduct.getTitle()).isNotEmpty();
        assertThat(firstProduct.getPrice()).isGreaterThan(0);
    }
    
    @Test
    public void testGetProductsPagination() throws IOException {
        ProductsResponse page1 = client.getProducts(10, 0);
        ProductsResponse page2 = client.getProducts(10, 10);
        
        assertThat(page1.getProducts()).hasSize(10);
        assertThat(page2.getProducts()).hasSize(10);
        assertThat(page1.getCurrentPage()).isEqualTo(1);
        assertThat(page2.getCurrentPage()).isEqualTo(2);
        
        // Ensure different products
        assertThat(page1.getProducts().get(0).getId())
            .isNotEqualTo(page2.getProducts().get(0).getId());
    }
    
    @Test
    public void testGetSingleProduct() throws IOException {
        Product product = client.getProduct(1);
        
        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo(1);
        assertThat(product.getTitle()).isNotEmpty();
        assertThat(product.getDescription()).isNotEmpty();
        assertThat(product.getCategory()).isNotEmpty();
        assertThat(product.getPrice()).isGreaterThan(0);
        assertThat(product.getStock()).isGreaterThanOrEqualTo(0);
        
        // Test helper methods
        if (product.getDiscountPercentage() > 0) {
            assertThat(product.isOnSale()).isTrue();
            assertThat(product.getDiscountedPrice()).isLessThan(product.getPrice());
        }
    }
    
    @Test
    public void testSearchProducts() throws IOException {
        ProductsResponse response = client.searchProducts("phone");
        
        assertThat(response).isNotNull();
        assertThat(response.getProducts()).isNotEmpty();
        
        // Check that results contain the search term
        boolean foundMatch = response.getProducts().stream()
            .anyMatch(p -> p.getTitle().toLowerCase().contains("phone") || 
                          p.getDescription().toLowerCase().contains("phone") ||
                          p.getCategory().toLowerCase().contains("phone"));
        assertThat(foundMatch).isTrue();
    }
    
    @Test
    public void testGetProductsByCategory() throws IOException {
        ProductsResponse response = client.getProductsByCategory("smartphones");
        
        assertThat(response).isNotNull();
        assertThat(response.getProducts()).isNotEmpty();
        
        // All products should be in the smartphones category
        response.getProducts().forEach(product -> 
            assertThat(product.getCategory()).isEqualTo("smartphones"));
    }
    
    @Test
    public void testGetUsers() throws IOException {
        UsersResponse response = client.getUsers();
        
        assertThat(response).isNotNull();
        assertThat(response.getUsers()).isNotEmpty();
        assertThat(response.getTotal()).isGreaterThan(0);
        
        User firstUser = response.getUsers().get(0);
        assertThat(firstUser.getId()).isGreaterThan(0);
        assertThat(firstUser.getFirstName()).isNotEmpty();
        assertThat(firstUser.getLastName()).isNotEmpty();
        assertThat(firstUser.getEmail()).isNotEmpty();
        assertThat(firstUser.getUsername()).isNotEmpty();
        
        // Test helper methods
        assertThat(firstUser.getFullName()).contains(firstUser.getFirstName());
        assertThat(firstUser.getFullName()).contains(firstUser.getLastName());
        
        if (firstUser.getHeight() > 0 && firstUser.getWeight() > 0) {
            assertThat(firstUser.getBMI()).isGreaterThan(0);
        }
    }
    
    @Test
    public void testGetSingleUser() throws IOException {
        User user = client.getUser(1);
        
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getFirstName()).isNotEmpty();
        assertThat(user.getEmail()).isNotEmpty();
        assertThat(user.getAge()).isGreaterThan(0);
        
        // Test optional fields
        if (user.getAddress().isPresent()) {
            Address address = user.getAddress().get();
            assertThat(address.getFullAddress()).isNotEmpty();
        }
    }
    
    @Test
    public void testSearchUsers() throws IOException {
        UsersResponse response = client.searchUsers("John");
        
        assertThat(response).isNotNull();
        assertThat(response.getUsers()).isNotEmpty();
        
        // Check that results contain the search term
        boolean foundMatch = response.getUsers().stream()
            .anyMatch(u -> u.getFullName().toLowerCase().contains("john") || 
                          u.getUsername().toLowerCase().contains("john"));
        assertThat(foundMatch).isTrue();
    }
    
    @Test
    public void testGetCarts() throws IOException {
        CartsResponse response = client.getCarts();
        
        assertThat(response).isNotNull();
        assertThat(response.getCarts()).isNotEmpty();
        assertThat(response.getTotal()).isGreaterThan(0);
        
        Cart firstCart = response.getCarts().get(0);
        assertThat(firstCart.getId()).isGreaterThan(0);
        assertThat(firstCart.getProducts()).isNotEmpty();
        assertThat(firstCart.getTotal()).isGreaterThan(0);
        assertThat(firstCart.getUserId()).isGreaterThan(0);
        
        // Test helper methods
        assertThat(firstCart.isEmpty()).isFalse();
        assertThat(firstCart.getTotalSavings()).isGreaterThanOrEqualTo(0);
        if (firstCart.getTotal() > firstCart.getDiscountedTotal()) {
            assertThat(firstCart.getSavingsPercentage()).isGreaterThan(0);
        }
    }
    
    @Test
    public void testGetSingleCart() throws IOException {
        Cart cart = client.getCart(1);
        
        assertThat(cart).isNotNull();
        assertThat(cart.getId()).isEqualTo(1);
        assertThat(cart.getProducts()).isNotEmpty();
        
        CartProduct firstProduct = cart.getProducts().get(0);
        assertThat(firstProduct.getId()).isGreaterThan(0);
        assertThat(firstProduct.getTitle()).isNotEmpty();
        assertThat(firstProduct.getQuantity()).isGreaterThan(0);
        assertThat(firstProduct.getPrice()).isGreaterThan(0);
        
        // Test helper methods
        assertThat(firstProduct.getUnitPrice()).isEqualTo(firstProduct.getPrice());
        assertThat(firstProduct.getDiscountedUnitPrice()).isGreaterThan(0);
    }
    
    @Test
    public void testAsyncOperations() throws ExecutionException, InterruptedException {
        CompletableFuture<ProductsResponse> productsAsync = client.getProductsAsync();
        CompletableFuture<UsersResponse> usersAsync = client.getUsersAsync();
        
        CompletableFuture<String> combined = productsAsync
            .thenCombine(usersAsync, (products, users) -> 
                "Products: " + products.getProducts().size() + 
                ", Users: " + users.getUsers().size());
        
        String result = combined.get();
        assertThat(result).contains("Products:");
        assertThat(result).contains("Users:");
        
        ProductsResponse products = productsAsync.get();
        UsersResponse users = usersAsync.get();
        
        assertThat(products.getProducts()).isNotEmpty();
        assertThat(users.getUsers()).isNotEmpty();
    }
    
    @Test
    public void testChainedAsyncOperations() throws ExecutionException, InterruptedException {
        String result = client.getProductsAsync()
            .thenApply(response -> response.getProducts().get(0))
            .thenCompose(product -> client.getUserAsync(1))
            .thenApply(user -> "Product: " + user.getFullName())
            .get();
        
        assertThat(result).isNotNull();
        assertThat(result).startsWith("Product:");
    }
}