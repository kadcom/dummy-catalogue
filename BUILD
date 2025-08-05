load("@rules_java//java:defs.bzl", "java_library", "java_test")
load("@rules_android//android:rules.bzl", "android_library")

# Main Java library (for JVM/Server usage)
java_library(
    name = "dummy-json-client",
    srcs = glob([
        "src/main/java/**/*.java",
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
        "src/main/java/**/*.java",
    ]),
    manifest = "src/main/AndroidManifest.xml",
    deps = [
        "@maven//:com_squareup_okhttp3_okhttp",
        "@maven//:com_squareup_moshi_moshi",
        "@maven//:com_squareup_moshi_moshi_adapters",
    ],
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
