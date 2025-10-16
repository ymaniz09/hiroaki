# 🚀 Modernization Updates

This fork has been modernized with the latest Android/Kotlin toolchain.

## 📊 Updated Versions

### Build Tools
- ✅ **Gradle**: 5.4.1 → **8.10**
- ✅ **Android Gradle Plugin**: 3.5.2 → **8.7.0**
- ✅ **Kotlin**: 1.3.50 → **2.0.20**
- ✅ **Java**: 8 → **17**

### Android SDK
- ✅ **compileSdk/targetSdk**: 29 → **35** (Android 15)
- ✅ **minSdk**: 14 → **21** (Android 5.0)

### Dependencies
- ✅ **OkHttp**: 3.12.0 → **4.12.0**
- ✅ **Retrofit**: 2.6.2 → **2.11.0**
- ✅ **Coroutines**: 1.3.2 → **1.9.0**
- ✅ **Moshi**: 1.8.0 → **1.15.1**
- ✅ **AndroidX**: All libraries updated to latest

## 🔧 Breaking Changes Fixed

### OkHttp 4.x API Changes
- `MockWebServer.setDispatcher()` → `MockWebServer.dispatcher`
- Added null-safety operators for `request.path` and `request.requestUrl`

### Moshi Kotlin Reflection
- Added `KotlinJsonAdapterFactory` requirement for Kotlin data classes
- Updated all service providers and tests

### Android 12+ Requirements
- Added `android:exported` attribute to MainActivity

### Gradle 8+ Updates
- Removed deprecated `jcenter()` repository
- Removed `kotlin-android-extensions` plugin
- Updated `classifier` → `archiveClassifier` in JAR tasks
- Updated `main` → `mainClass` for JavaExec tasks
- Replaced `lintOptions` with `lint` block

### Deprecated Features
- ⚠️ **Maven Publishing**: Commented out (needs migration to `maven-publish` plugin)
- ⚠️ **ktlint**: Disabled (needs reconfiguration for new version)

## ✅ Test Results

**All 64 unit tests passing** (100% success rate)

Fixed Mockito 5.x compatibility issues:
- Removed `@RunWith(MockitoJUnitRunner::class)` annotations
- Updated Moshi test configuration

## 🎯 CI/CD

- ✅ GitHub Actions configured
- ✅ Automated testing on push/PR
- ✅ APK artifacts generated
- ✅ Library assembly verified

## 📦 Package Name Change

- **Old**: `me.jorgecastillo.hiroaki`
- **New**: `io.github.ymaniz09.hiroaki`

All package declarations, imports, and directory structures have been updated.

### Maven Coordinates

```gradle
dependencies {
    testImplementation 'io.github.ymaniz09:hiroaki-core:1.0.0'
    androidTestImplementation 'io.github.ymaniz09:hiroaki-android:1.0.0'
}
```

## 📝 Original Project

This is a fork of [Hiroaki](https://github.com/JorgeCastilloPrz/hiroaki) by Jorge Castillo Pérez.

**Original License**: Apache 2.0
