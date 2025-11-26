# 🔒 App Lock

A modern, lightweight Android app locker for Samsung phones.

## Features

✅ **Multiple Authentication Methods**
- PIN Lock (4-digit)
- Pattern Lock (3x3 grid)
- Fingerprint/Biometric

✅ **Security Features**
- Intruder Selfie (captures photo on failed attempts)
- Fake Crash Screen
- Encrypted credential storage

✅ **Modern Design**
- Material Design 3
- Professional & minimalistic UI
- Dark mode support

## Quick Start

1. See [BUILD_GUIDE.md](BUILD_GUIDE.md) for build instructions
2. Install on your phone
3. Set up PIN/Pattern
4. Enable Accessibility Service
5. Select apps to lock

## Requirements

- Android 8.0 (API 26) or higher
- Samsung phone (or any Android device)

## Permissions

- **Accessibility** - Detect app launches
- **Overlay** - Display lock screens
- **Camera** - Intruder selfie (optional)
- **Biometric** - Fingerprint unlock (optional)

## Build

```bash
# Using Gradle
./gradlew assembleDebug

# Or use Android Studio
Build → Build APK(s)
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Project Structure

- `data/` - Room database, entities, DAOs
- `service/` - Accessibility monitoring service
- `ui/` - All activities and custom views
- `util/` - Security utilities, helpers
- `manager/` - Intruder photo capture

## License

Personal use project

---

**Created with ❤️ for enhanced privacy and security**
