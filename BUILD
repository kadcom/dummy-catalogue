load("@rules_java//java:defs.bzl", "java_library", "java_test")
load("@rules_android//android:rules.bzl", "android_library", "android_binary")

# Main Java library (for JVM/Server usage)
java_library(
    name = "dummy-json-client",
    srcs = glob([
        "src/main/java/dev/kadcom/dummyjson/**/*.java",
    ]),
    deps = [
        "@maven//:com_squareup_okhttp3_okhttp",
        "@maven//:com_squareup_moshi_moshi",
        "@maven//:com_squareup_moshi_moshi_adapters",
    ],
    visibility = ["//visibility:public"],
)

# Android library (for AAR usage) using Starlark android_library
android_library(
    name = "dummy-json-client-android",
    srcs = glob([
        "src/main/java/dev/kadcom/dummyjson/**/*.java",
    ]),
    manifest = "src/main/AndroidManifest.xml",
    deps = [
        "@maven//:com_squareup_okhttp3_okhttp",
        "@maven//:com_squareup_moshi_moshi",
        "@maven//:com_squareup_moshi_moshi_adapters",
    ],
    visibility = ["//visibility:public"],
)

# Android Commerce App Library
android_library(
    name = "commerce-app-lib",
    srcs = glob([
        "src/main/java/dev/kadcom/commerce/**/*.java",
    ]),
    manifest = "src/main/AndroidManifest.xml",
    deps = [
        ":dummy-json-client-android",
        "@maven//:com_squareup_okhttp3_okhttp",
        "@maven//:androidx_recyclerview_recyclerview",
        "@maven//:androidx_swiperefreshlayout_swiperefreshlayout", 
        "@maven//:androidx_viewpager2_viewpager2",
    ],
    visibility = ["//visibility:public"],
)

# Android Commerce App APK
android_binary(
    name = "commerce-app",
    deps = [":commerce-app-lib"],
    manifest = "src/main/AndroidManifest.xml",
    visibility = ["//visibility:public"],
)

# Test suite
java_test(
    name = "dummy-json-client-test",
    srcs = glob([
        "src/test/java/**/*.java",
    ]),
    test_class = "dev.kadcom.dummyjson.DummyJsonClientTestSuite",
    deps = [
        ":dummy-json-client",
        "@maven//:junit_junit",
        "@maven//:org_mockito_mockito_core",
        "@maven//:com_squareup_okhttp3_mockwebserver",
        "@maven//:org_assertj_assertj_core",
    ],
    size = "medium",
    testonly = True,
)

# Objective-C library
objc_library(
    name = "dummy-json-client-objc",
    srcs = glob([
        "src/main/objc/*.m",
    ]),
    hdrs = glob([
        "src/main/objc/*.h", 
    ]),
    visibility = ["//visibility:public"],
)

# Note: Apple unit tests (iOS/macOS) require compatible Bazel/rules_apple versions
# The Objective-C library and test library build successfully
# Tests can be run directly with Xcode or compatible test runner

objc_library(
    name = "dummy-json-client-objc-test-lib", 
    srcs = glob([
        "src/test/objc/*.m",
    ]),
    deps = [
        ":dummy-json-client-objc",
    ],
    testonly = True,
)
