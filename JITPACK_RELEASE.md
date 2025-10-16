# 🚀 JitPack Release Guide

## How to Publish a New Version

### 1. Prepare the Release

Make sure all tests pass:
```bash
./gradlew test
./gradlew build
```

### 2. Create a Git Tag

```bash
# Version format: MAJOR.MINOR.PATCH
git tag -a 1.0.0 -m "Release version 1.0.0 - Initial modernized fork

- Gradle 8.10
- Kotlin 2.0.20
- Android SDK 35
- Package renamed to io.github.ymaniz09.hiroaki
- All tests passing (64/64)
"

# View your tags
git tag -l
```

### 3. Push the Tag

```bash
# Push the tag to GitHub
git push origin 1.0.0

# Or push all tags
git push origin --tags
```

### 4. Create GitHub Release (Optional but Recommended)

Go to: https://github.com/ymaniz09/hiroaki/releases/new

1. Choose the tag you just created: `1.0.0`
2. Set release title: `v1.0.0 - Modernized Fork`
3. Add release notes (see template below)
4. Click "Publish release"

### 5. Trigger JitPack Build

Visit: https://jitpack.io/#ymaniz09/hiroaki

- Click "Look up" or "Get it"
- JitPack will automatically build your library
- Wait for the green checkmark ✅

### 6. Test the Release

Create a test project and add:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    testImplementation 'com.github.ymaniz09:hiroaki:1.0.0'
}
```

## Release Notes Template

```markdown
## 🚀 Release v1.0.0 - Modernized Fork

This is the first release of the modernized Hiroaki fork.

### ✨ What's New

- **Gradle 8.10** (from 5.4.1)
- **Kotlin 2.0.20** (from 1.3.50)
- **Android SDK 35** (from 29)
- **Java 17** (from 8)
- **OkHttp 4.12.0** (from 3.12.0)
- **Package renamed** to `io.github.ymaniz09.hiroaki`

### 🔧 Breaking Changes

- Minimum SDK raised to 21 (from 14)
- Package name changed from `me.jorgecastillo.hiroaki`
- Java 17 required

### 📦 Installation

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    testImplementation 'com.github.ymaniz09:hiroaki:1.0.0'
}
```

### 📚 Documentation

- [Migration Guide](MIGRATION_GUIDE.md)
- [Modernization Details](MODERNIZATION.md)
- [Original Project](https://github.com/JorgeCastilloPrz/hiroaki)

### ✅ Testing

All 64 unit tests passing (100% success rate)

### 📄 License

Apache 2.0 (same as original)

---

**Full Changelog**: https://github.com/ymaniz09/hiroaki/compare/0.2.3...1.0.0
```

## Version Numbering

This project follows [Semantic Versioning](https://semver.org/):

- **MAJOR** (1.x.x): Incompatible API changes
- **MINOR** (x.1.x): New functionality (backwards-compatible)
- **PATCH** (x.x.1): Bug fixes (backwards-compatible)

### Current Version: 1.0.0

Starting with 1.0.0 because this is a major rewrite with:
- New package name
- Updated dependencies
- Breaking changes from original

## JitPack Badge

After publishing, add this to README:

```markdown
[![JitPack](https://jitpack.io/v/ymaniz09/hiroaki.svg)](https://jitpack.io/#ymaniz09/hiroaki)
```

## Troubleshooting

### Build Fails on JitPack

1. Check build log: https://jitpack.io/com/github/ymaniz09/hiroaki/1.0.0/build.log
2. Common issues:
   - JDK version mismatch (make sure `jitpack.yml` specifies JDK 17)
   - Missing dependencies
   - Test failures

### Version Not Found

- Wait a few minutes for JitPack to build
- Clear JitPack cache: Add `?#refresh` to the JitPack URL
- Verify tag exists: `git tag -l`

### Users Can't Download

- Make sure repository is public
- Check if build succeeded on JitPack
- Verify users added JitPack repository

## Future Releases

For subsequent releases:

```bash
# Increment version
git tag -a 1.1.0 -m "Release version 1.1.0 - Feature additions"
git push origin 1.1.0

# JitPack will automatically build the new version
```

## Contact

Issues: https://github.com/ymaniz09/hiroaki/issues

