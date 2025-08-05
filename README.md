# HttpBin Java Client

A pure Java library for interacting with [httpbin.org](https://httpbin.org) APIs, compatible with both JVM and Android platforms. Built with Bazel and supports both Maven JAR and Android AAR distribution.

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
HttpBinClient client = new HttpBinClient();

// Synchronous requests
HttpBinResponse response = client.get("/get");
System.out.println("Origin: " + response.getOrigin().orElse("unknown"));

// POST with JSON
String json = "{\"message\":\"Hello HttpBin\"}";
HttpBinResponse postResponse = client.post("/post", json);
```

### With OkHttp (Recommended)
```java
// Use OkHttp for better performance and HTTP/2 support
HttpBinClient client = HttpBinClient.withOkHttp();

// Or provide your own OkHttp instance
OkHttpClient okHttp = new OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .build();
HttpBinClient client = new HttpBinClient(okHttp);
```

### Async Operations
```java
// CompletableFuture-based async operations
CompletableFuture<HttpBinResponse> future = client.getAsync("/get");

// Chain operations
client.getAsync("/get")
    .thenApply(response -> response.getOrigin().orElse("unknown"))
    .thenCompose(origin -> client.postAsync("/post", "{\"origin\":\"" + origin + "\"}"))
    .thenAccept(System.out::println);

// Combine multiple requests
CompletableFuture<String> combined = client.getAsync("/get")
    .thenCombine(client.getAsync("/ip"), (resp1, resp2) -> 
        "Response 1: " + resp1.getOrigin() + ", Response 2: " + resp2.getOrigin());
```

### HttpBin Specific Endpoints
```java
// Status codes
StatusResponse status = client.status(200);
StatusResponse notFound = client.status(404);

// Delay
DelayResponse delayed = client.delay(2); // 2 second delay

// Cookies
CookieResponse cookies = client.cookies();
String sessionId = cookies.getCookie("session").orElse("no-session");
```

### Configuration
```java
HttpBinClient client = new HttpBinClient()
    .setTimeout(5000) // 5 second timeout
    .addDefaultHeader("User-Agent", "MyApp/1.0")
    .addDefaultHeader("X-API-Key", "secret");
```

### Android Usage
```java
public class MainActivity extends AppCompatActivity {
    private HttpBinClient httpBinClient;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize client (works the same as JVM)
        httpBinClient = HttpBinClient.withOkHttp();
        
        // Use async methods to avoid blocking UI thread
        httpBinClient.getAsync("/get")
            .thenAccept(response -> runOnUiThread(() -> {
                // Update UI with response
                textView.setText("Origin: " + response.getOrigin().orElse("unknown"));
            }));
    }
}
```

## Building with Bazel

### Build JAR
```bash
bazel build //:httpbin-client
```

### Build Android AAR
```bash
bazel build //:httpbin-client-android
```

### Run Tests
```bash
bazel test //:httpbin-client-test
```

### Integration Tests (requires internet)
```bash
bazel test //src/test/java/dev/kadcom/httpbin/integration:all
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
HttpBinResponse response = client.get("/get");

// Safe access to nullable fields
String origin = response.getOrigin().orElse("unknown");
Map<String, String> headers = response.getHeaders(); // Never null
Optional<String> userAgent = response.getHeader("User-Agent");
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

MIT License - see LICENSE file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Run `bazel test //...` to ensure all tests pass
5. Submit a pull request