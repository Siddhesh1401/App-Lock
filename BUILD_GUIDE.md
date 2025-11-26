# App Lock - Build Instructions

## 📱 Project Complete!

Your App Lock application is now ready! This is a fully functional Android app with:

✅ **PIN Lock** - 4-digit numeric PIN protection  
✅ **Pattern Lock** - 9-dot pattern drawing  
✅ **Fingerprint/Biometric** - Hardware biometric support  
✅ **Intruder Selfie** - Front camera photo on failed attempts  
✅ **Fake Crash Screen** - Disguise lock as app crash  
✅ **Modern Material Design 3** - Professional, minimalistic UI  
✅ **Accessibility Service** - Efficient app monitoring  

## 🔨 Building the APK

You have **3 options** to build the APK:

### Option 1: Android Studio (Recommended - Easiest)

1. **Download Android Studio**: https://developer.android.com/studio
2. **Install Android Studio** (follow the installer)
3. **Open the project**:
   - Launch Android Studio
   - Click "Open" and select: `C:\Users\SIDDHESH\Desktop\PROJECTS\app lock`
   - Wait for Gradle sync to complete (first time takes a few minutes)
4. **Build APK**:
   - Click **Build** menu → **Build Bundle(s) / APK(s)** → **Build APK(s)**
   - Wait for build to complete
   - Click **"locate"** in the notification to find your APK
   - APK location: `app\build\outputs\apk\debug\app-debug.apk`

### Option 2: Command Line (Fast, if you have SDK)

If you have Android SDK installed:

```bash
cd "C:\Users\SIDDHESH\Desktop\PROJECTS\app lock"
gradlew assembleDebug
```

The APK will be at: `app\build\outputs\apk\debug\app-debug.apk`

### Option 3: Online Build Service

Use GitHub Actions or other CI/CD (requires git setup)

## 📲 Installing on Your Samsung Phone

1. **Transfer APK** to your phone (via USB, cloud, email, etc.)
2. **Enable Unknown Sources**:
   - Settings → Apps → Special Access → Install Unknown Apps
   - Allow for your file browser/installer
3. **Install APK**: Tap the APK file and install
4. **Grant Permissions**:
   - Accessibility Permission (crucial!)
   - Overlay Permission
   - Camera Permission (for intruder selfie)

## 🚀 First Time Setup

1. Open **App Lock** app
2. **Create your PIN or Pattern** (first launch setup)
3. **Enable Accessibility Service**:
   - Click the status card in main screen
   - Enable "App Lock" in Accessibility settings
4. **Select Apps to Lock**:
   - Tap "Lock Apps" button
   - Toggle on apps you want to protect
5. **Configure Settings** (optional):
   - Enable Intruder Selfie
   - Enable Fake Crash Screen
   - Enable Biometric

## ⚙️ Settings Available

- **Intruder Selfie**: Captures front camera photo after wrong attempts
- **Fake Crash Screen**: Shows fake "app stopped" message
- **Biometric**: Use fingerprint instead of PIN/pattern
- **View Intruder Photos**: See captured intruder selfies

## 🔧 Troubleshooting

**Lock screen doesn't appear?**
- Make sure Accessibility Service is enabled
- Check Overlay Permission is granted

**Can't take intruder photos?**
- Grant Camera permission in Settings

**App doesn't detect locked apps?**
- Re-enable Accessibility Service
- Restart your phone

## 📦 Project Structure

```
app lock/
├── app/
│   ├── src/main/
│   │   ├── java/com/applock/secure/
│   │   │   ├── data/           # Database (Room)
│   │   │   ├── manager/        # Intruder photo capture
│   │   │   ├── service/        # Accessibility monitoring
│   │   │   ├── ui/             # All activities & views
│   │   │   └── util/           # Security & utilities
│   │   ├── res/                # Layouts, colors, strings
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/                     # Gradle wrapper
├── build.gradle.kts
└── settings.gradle.kts
```

## 🎨 Features Explained

### PIN Lock
- 4-digit numeric PIN
- Clean numeric keypad
- Shake animation on wrong PIN

### Pattern Lock
- 3x3 dot grid (9 dots)
- Minimum 4 dots required
- Visual feedback while drawing

### Biometric
- Uses hardware fingerprint sensor
- Fallback to PIN if biometric fails
- Android native BiometricPrompt

### Intruder Selfie
- Captures photo after 2 failed attempts
- Uses front camera
- Saved locally with timestamp
- View gallery in app

### Fake Crash
- Looks like real Android crash dialog
- Long press anywhere to reveal real lock
- Fools casual intruders

---

**Need Help?** If you encounter any issues during build, let me know!
