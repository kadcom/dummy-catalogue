# DummyJSON Java Client & Android Commerce App

A pure Java library for interacting with [DummyJSON](https://dummyjson.com) APIs, featuring a high-performance Android e-commerce showcase app. Built with Bazel and supports both Maven JAR and Android AAR distribution.

## 🚀 Android Commerce App

**High-performance e-commerce showcase** demonstrating the DummyJSON client with optimized Canvas rendering:

### Building the Android App

```bash
# Build Android Commerce APK (with ProGuard optimization)
bazel build :commerce-app

# Install to connected Android device/emulator
bazel mobile-install :commerce-app

# Build for debugging (faster, no optimization)
bazel build -c dbg :commerce-app
```

### App Features

- **Pure Canvas rendering** for 60fps scrolling performance
- **Hybrid caching system** eliminates overdraw (red → blue/none) 
- **Async image loading** with LRU memory management
- **ProGuard optimization** reduces APK to 1.7MB
- **Scroll-aware animations** pause during fast scrolling
- **Material Design** implemented programmatically (no XML)

### Performance Testing

```bash
# Enable overdraw visualization on device
adb shell setprop debug.hwui.overdraw show

# Disable overdraw visualization  
adb shell setprop debug.hwui.overdraw false
```

📖 **See [ARCHITECTURE.md](./ARCHITECTURE.md) for detailed technical documentation.**

## 📦 DummyJSON Java Client Library

### Building from Source

**Requirements:**
- Java 17+ 
- [Bazel](https://bazel.build) 6.0+
- Android SDK (set `ANDROID_HOME` environment variable)

```bash
# Build Java library
bazel build :dummy-json-client

# Build Android AAR  
bazel build :dummy-json-client-android

# Build Android Commerce App
bazel build :commerce-app

# Run tests
bazel test :dummy-json-client-test

# Build everything
bazel build //...
```

## Features

- **Dual Platform Support**: Works on JVM (Java 8+) and Android
- **Optional Dependencies**: Use with or without OkHttp
- **Async Support**: CompletableFuture-based async operations
- **JSON Parsing**: Powered by Moshi
- **Bazel Build System**: Fast, reliable builds
- **Comprehensive Testing**: Unit tests with MockWebServer and integration tests

## Quick Start

### Maven Dependency
```xml
<dependency>
    <groupId>dev.kadcom</groupId>
    <artifactId>httpbin-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Android Gradle
```kotlin
implementation 'dev.kadcom:httpbin-client:1.0.0'
```

## Usage Examples

### Basic Usage (HttpURLConnection)
```java
// Default client uses HttpURLConnection (no external dependencies)
DummyJsonClient client = new DummyJsonClient();

// Get all products
ProductsResponse products = client.getProducts();
System.out.println("Found " + products.getTotal() + " products");

// Get specific product
Product product = client.getProduct(1);
System.out.println("Product: " + product.getTitle());
```

### With OkHttp (Recommended)
```java
// Use OkHttp for better performance and HTTP/2 support
DummyJsonClient client = DummyJsonClient.withOkHttp();

// Or provide your own OkHttp instance
OkHttpClient okHttp = new OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .build();
DummyJsonClient client = new DummyJsonClient(okHttp);
```

### Async Operations
```java
// CompletableFuture-based async operations
CompletableFuture<ProductsResponse> future = client.getProductsAsync();

// Chain operations
client.getProductAsync(1)
    .thenApply(Product::getCategory)
    .thenCompose(category -> client.getProductsByCategoryAsync(category))
    .thenAccept(System.out::println);

// Combine multiple requests
CompletableFuture<String> combined = client.getProductAsync(1)
    .thenCombine(client.getProductAsync(2), (p1, p2) -> 
        "Product 1: " + p1.getTitle() + ", Product 2: " + p2.getTitle());
```

### DummyJSON Specific Endpoints
```java
// Categories
List<String> categories = client.getCategories();

// Products by category
ProductsResponse smartphones = client.getProductsByCategory("smartphones");

// Search products
ProductsResponse results = client.searchProducts("laptop");

// Pagination
ProductsResponse page2 = client.getProducts(10, 20); // limit=10, skip=20
```

### Configuration
```java
DummyJsonClient client = DummyJsonClient.withOkHttp()
    .setTimeout(5000) // 5 second timeout
    .addDefaultHeader("User-Agent", "MyApp/1.0");
```

### Android Usage
```java
public class MainActivity extends AppCompatActivity {
    private DummyJsonClient dummyJsonClient;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize client (works the same as JVM)
        dummyJsonClient = DummyJsonClient.withOkHttp();
        
        // Use async methods to avoid blocking UI thread
        dummyJsonClient.getProductsAsync()
            .thenAccept(response -> runOnUiThread(() -> {
                // Update UI with response
                textView.setText("Found " + response.getTotal() + " products");
            }));
    }
}
```

## Building with Bazel

### Build JAR
```bash
bazel build //:dummy-json-client
```

### Build Android AAR
```bash
bazel build //:dummy-json-client-android
```

### Build Android Commerce App
```bash
bazel build //:commerce-app
```

### Run Tests
```bash
bazel test //:dummy-json-client-test
```

## Architecture

### Optional Dependencies
The library is designed with optional dependencies:

- **Core**: Pure Java, uses HttpURLConnection
- **Enhanced**: Add OkHttp for better performance
- **JSON**: Moshi for fast JSON parsing

### Response Models
All response models use Java 8+ Optional for null-safe access:

```java
ProductsResponse response = client.getProducts();

// Safe access to nullable fields
List<Product> products = response.getProducts(); // Never null
int total = response.getTotal();
Optional<String> brand = product.getBrand();
```

## Compatibility

- **Java**: 8+ (lambdas, streams, CompletableFuture)
- **Android**: API 21+ (when using OkHttp), API 19+ (HttpURLConnection only)
- **Build**: Bazel 6.0+

## Dependencies

### Runtime (Optional)
- `com.squareup.okhttp3:okhttp:4.12.0` (optional)
- `com.squareup.moshi:moshi:1.15.0`

### Test
- `junit:junit:4.13.2`
- `com.squareup.okhttp3:mockwebserver:4.12.0`
- `org.assertj:assertj-core:3.24.2`

## License

GPL v3 License - see COPYING file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Run `bazel test //...` to ensure all tests pass
5. Submit a pull request
