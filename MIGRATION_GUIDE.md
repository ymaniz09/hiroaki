# 🔄 Migration Guide

## Migrating from Original Hiroaki

If you're migrating from the original `me.jorgecastillo:hiroaki` to this modernized fork:

### 1. Update Dependencies

**Before:**
```gradle
dependencies {
    testImplementation 'me.jorgecastillo:hiroaki-core:0.2.3'
    androidTestImplementation 'me.jorgecastillo:hiroaki-android:0.2.3'
}
```

**After (via JitPack):**
```gradle
// In your root build.gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

// In your app/build.gradle
dependencies {
    testImplementation 'com.github.ymaniz09.hiroaki:hiroaki-core:1.0.0'
    androidTestImplementation 'com.github.ymaniz09.hiroaki:hiroaki-android:1.0.0'
}

// Or use the short form (both modules)
dependencies {
    testImplementation 'com.github.ymaniz09:hiroaki:1.0.0'
}
```

### Latest Version

Check the latest version at: https://jitpack.io/#ymaniz09/hiroaki

[![JitPack](https://jitpack.io/v/ymaniz09/hiroaki.svg)](https://jitpack.io/#ymaniz09/hiroaki)

### 2. Update Imports

**Before:**
```kotlin
import me.jorgecastillo.hiroaki.*
import me.jorgecastillo.hiroaki.internal.MockServerSuite
import me.jorgecastillo.hiroaki.models.*
```

**After:**
```kotlin
import io.github.ymaniz09.hiroaki.*
import io.github.ymaniz09.hiroaki.internal.MockServerSuite
import io.github.ymaniz09.hiroaki.models.*
```

### 3. Find & Replace

Use your IDE's "Replace in Path" feature:
- Find: `me.jorgecastillo.hiroaki`
- Replace: `io.github.ymaniz09.hiroaki`

### 4. API Compatibility

✅ **100% API compatible** - No breaking changes in the API
✅ All methods and classes work the same way
✅ Only package names changed

### 5. Minimum Requirements

⚠️ **Note**: This fork requires higher minimum versions:
- **Min SDK**: 21 (was 14)
- **Java**: 17 (was 8)  
- **Gradle**: 8.0+ (was 5.4)
- **Kotlin**: 1.9+ (was 1.3)

### 6. New Features

- ✅ Kotlin 2.0 support
- ✅ Android SDK 35 support
- ✅ OkHttp 4.x support
- ✅ Modern coroutines (1.9.0)
- ✅ All tests updated and passing

## Example Migration

**Before:**
```kotlin
import me.jorgecastillo.hiroaki.internal.MockServerSuite
import me.jorgecastillo.hiroaki.models.success
import me.jorgecastillo.hiroaki.models.fileBody

class MyTest : MockServerSuite() {
    // your test code
}
```

**After:**
```kotlin
import io.github.ymaniz09.hiroaki.internal.MockServerSuite
import io.github.ymaniz09.hiroaki.models.success
import io.github.ymaniz09.hiroaki.models.fileBody

class MyTest : MockServerSuite() {
    // your test code - no changes needed!
}
```

## Need Help?

Open an issue at: https://github.com/ymaniz09/hiroaki/issues
