# Build Commands Reference

## 🚀 Build Commands for DummyJSON Client + Android Commerce App

### Core Library Builds

```bash
# Build Java library (for server/JVM usage)
bazel build //:dummy-json-client

# Run library tests  
bazel test //:dummy-json-client-test

# Build Android AAR (library distribution)
bazel build //:dummy-json-client-android
```

### Android Commerce App Builds

```bash
# Build Android Commerce app library
bazel build //:commerce-app-lib

# Build complete Android APK
bazel build //:commerce-app

# Install APK on device/emulator
adb install bazel-bin/commerce-app.apk
```

### Generated Artifacts

| Target | Output | Purpose |
|--------|--------|---------|
| `//:dummy-json-client` | `libdummy-json-client.jar` | Java library for server/JVM |
| `//:dummy-json-client-android` | `libdummy-json-client-android.jar` | Android library JAR |
| `//:commerce-app-lib` | `libcommerce-app-lib.jar` | Android commerce app library |
| `//:commerce-app` | `commerce-app.apk` | **Complete Android APK** |

### APK Details

- **File**: `bazel-bin/commerce-app.apk` (2.7MB)
- **Package**: `dev.kadcom.commerce`
- **Min SDK**: Android API 21+ (Lollipop)
- **Features**: Product grid, details, async image loading, smooth scrolling

### Performance Optimizations Included

✅ **OkHttp async networking** with connection pooling  
✅ **LRU image cache** with automatic memory management  
✅ **Canvas-based rendering** for 60+ FPS scrolling  
✅ **RecyclerView optimization** with ViewHolder pattern  
✅ **Material Design** styling without XML layouts  

### Development Workflow

```bash
# 1. Clean build
bazel clean

# 2. Test library
bazel test //:dummy-json-client-test

# 3. Build Android app
bazel build //:commerce-app

# 4. Install & test on device
adb install bazel-bin/commerce-app.apk
```

---

## 📱 App Architecture

The Android app demonstrates high-performance techniques:
- **Pure Java** (no Kotlin/Compose)
- **No XML layouts** (programmatic UI)
- **Custom Canvas drawing** (no ImageView overhead)
- **Async OkHttp** with intelligent caching
- **Material Design** with smooth animations

**Perfect showcase of the DummyJSON client library in action!**