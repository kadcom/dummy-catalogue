package dev.kadcom.dummyjson.client;

import dev.kadcom.dummyjson.models.*;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import okhttp3.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DummyJsonClient {
    private static final String DEFAULT_BASE_URL = "https://dummyjson.com";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private final String baseUrl;
    private final OkHttpClient okHttpClient;
    private final boolean useOkHttp;
    private final Moshi moshi;
    private final Executor executor;
    private final Map<String, String> defaultHeaders;
    private int timeoutMs = 30000;

    public DummyJsonClient() {
        this(DEFAULT_BASE_URL, null);
    }
    
    public DummyJsonClient(String baseUrl) {
        this(baseUrl, null);
    }
    
    public DummyJsonClient(OkHttpClient okHttpClient) {
        this(DEFAULT_BASE_URL, okHttpClient);
    }
    
    public DummyJsonClient(String baseUrl, OkHttpClient okHttpClient) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.okHttpClient = okHttpClient;
        this.useOkHttp = okHttpClient != null;
        this.moshi = new Moshi.Builder().build();
        this.executor = Executors.newCachedThreadPool();
        this.defaultHeaders = new HashMap<>();
        // Always request JSON responses
        this.defaultHeaders.put("Accept", "application/json");
        this.defaultHeaders.put("Content-Type", "application/json");
    }
    
    public static DummyJsonClient withOkHttp() {
        try {
            OkHttpClient client = new OkHttpClient.Builder().build();
            return new DummyJsonClient(client);
        } catch (Exception e) {
            throw new RuntimeException("OkHttp not found on classpath", e);
        }
    }
    
    public DummyJsonClient setTimeout(int timeoutMs) {
        this.timeoutMs = timeoutMs;
        return this;
    }
    
    public DummyJsonClient addDefaultHeader(String key, String value) {
        this.defaultHeaders.put(key, value);
        return this;
    }

    // ============ PRODUCTS API ============
    
    public ProductsResponse getProducts() throws IOException {
        return getProducts(30, 0);
    }
    
    public ProductsResponse getProducts(int limit, int skip) throws IOException {
        String endpoint = String.format("/products?limit=%d&skip=%d", limit, skip);
        String response = executeRawRequest("GET", endpoint, null);
        JsonAdapter<ProductsResponse> adapter = moshi.adapter(ProductsResponse.class);
        return adapter.fromJson(response);
    }
    
    public Product getProduct(int id) throws IOException {
        String response = executeRawRequest("GET", "/products/" + id, null);
        JsonAdapter<Product> adapter = moshi.adapter(Product.class);
        return adapter.fromJson(response);
    }
    
    public ProductsResponse searchProducts(String query) throws IOException {
        return searchProducts(query, 30, 0);
    }
    
    public ProductsResponse searchProducts(String query, int limit, int skip) throws IOException {
        String endpoint = String.format("/products/search?q=%s&limit=%d&skip=%d", query, limit, skip);
        String response = executeRawRequest("GET", endpoint, null);
        JsonAdapter<ProductsResponse> adapter = moshi.adapter(ProductsResponse.class);
        return adapter.fromJson(response);
    }
    
    public ProductsResponse getProductsByCategory(String category) throws IOException {
        return getProductsByCategory(category, 30, 0);
    }
    
    public ProductsResponse getProductsByCategory(String category, int limit, int skip) throws IOException {
        String endpoint = String.format("/products/category/%s?limit=%d&skip=%d", category, limit, skip);
        String response = executeRawRequest("GET", endpoint, null);
        JsonAdapter<ProductsResponse> adapter = moshi.adapter(ProductsResponse.class);
        return adapter.fromJson(response);
    }

    // ============ USERS API ============
    
    public UsersResponse getUsers() throws IOException {
        return getUsers(30, 0);
    }
    
    public UsersResponse getUsers(int limit, int skip) throws IOException {
        String endpoint = String.format("/users?limit=%d&skip=%d", limit, skip);
        String response = executeRawRequest("GET", endpoint, null);
        JsonAdapter<UsersResponse> adapter = moshi.adapter(UsersResponse.class);
        return adapter.fromJson(response);
    }
    
    public User getUser(int id) throws IOException {
        String response = executeRawRequest("GET", "/users/" + id, null);
        JsonAdapter<User> adapter = moshi.adapter(User.class);
        return adapter.fromJson(response);
    }
    
    public UsersResponse searchUsers(String query) throws IOException {
        return searchUsers(query, 30, 0);
    }
    
    public UsersResponse searchUsers(String query, int limit, int skip) throws IOException {
        String endpoint = String.format("/users/search?q=%s&limit=%d&skip=%d", query, limit, skip);
        String response = executeRawRequest("GET", endpoint, null);
        JsonAdapter<UsersResponse> adapter = moshi.adapter(UsersResponse.class);
        return adapter.fromJson(response);
    }

    // ============ CARTS API ============
    
    public CartsResponse getCarts() throws IOException {
        return getCarts(30, 0);
    }
    
    public CartsResponse getCarts(int limit, int skip) throws IOException {
        String endpoint = String.format("/carts?limit=%d&skip=%d", limit, skip);
        String response = executeRawRequest("GET", endpoint, null);
        JsonAdapter<CartsResponse> adapter = moshi.adapter(CartsResponse.class);
        return adapter.fromJson(response);
    }
    
    public Cart getCart(int id) throws IOException {
        String response = executeRawRequest("GET", "/carts/" + id, null);
        JsonAdapter<Cart> adapter = moshi.adapter(Cart.class);
        return adapter.fromJson(response);
    }
    
    public CartsResponse getUserCarts(int userId) throws IOException {
        String response = executeRawRequest("GET", "/carts/user/" + userId, null);
        JsonAdapter<CartsResponse> adapter = moshi.adapter(CartsResponse.class);
        return adapter.fromJson(response);
    }

    // ============ ASYNC METHODS ============
    
    public CompletableFuture<ProductsResponse> getProductsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getProducts();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }
    
    public CompletableFuture<Product> getProductAsync(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getProduct(id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }
    
    public CompletableFuture<UsersResponse> getUsersAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getUsers();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }
    
    public CompletableFuture<User> getUserAsync(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getUser(id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }

    // ============ PRIVATE HELPER METHODS ============
    
    private String executeRawRequest(String method, String endpoint, String body) throws IOException {
        if (useOkHttp) {
            return executeOkHttpRequest(method, endpoint, body);
        } else {
            return executeHttpUrlConnectionRequest(method, endpoint, body);
        }
    }
    
    private String executeOkHttpRequest(String method, String endpoint, String body) throws IOException {
        String url = baseUrl + (endpoint.startsWith("/") ? endpoint : "/" + endpoint);
        
        Request.Builder requestBuilder = new Request.Builder().url(url);
        
        for (Map.Entry<String, String> header : defaultHeaders.entrySet()) {
            requestBuilder.header(header.getKey(), header.getValue());
        }
        
        RequestBody requestBody = null;
        if (body != null) {
            requestBody = RequestBody.create(body, JSON);
        }
        
        switch (method.toUpperCase()) {
            case "GET":
                requestBuilder.get();
                break;
            case "POST":
                requestBuilder.post(requestBody != null ? requestBody : RequestBody.create("", null));
                break;
            case "PUT":
                requestBuilder.put(requestBody != null ? requestBody : RequestBody.create("", null));
                break;
            case "DELETE":
                if (requestBody != null) {
                    requestBuilder.delete(requestBody);
                } else {
                    requestBuilder.delete();
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
        
        Request request = requestBuilder.build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() == null) {
                return "";
            }
            return response.body().string();
        }
    }
    
    private String executeHttpUrlConnectionRequest(String method, String endpoint, String body) throws IOException {
        String urlString = baseUrl + (endpoint.startsWith("/") ? endpoint : "/" + endpoint);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try {
            connection.setRequestMethod(method);
            connection.setConnectTimeout(timeoutMs);
            connection.setReadTimeout(timeoutMs);
            
            for (Map.Entry<String, String> header : defaultHeaders.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
            
            if (body != null) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = body.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }
            
            int responseCode = connection.getResponseCode();
            InputStream inputStream = responseCode >= 200 && responseCode < 300 
                ? connection.getInputStream() 
                : connection.getErrorStream();
                
            if (inputStream == null) {
                return "";
            }
            
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } finally {
            connection.disconnect();
        }
    }
}