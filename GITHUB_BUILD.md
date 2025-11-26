# 🔒 App Lock - GitHub Build Instructions

## 🚀 Quick Start (Using GitHub Actions)

Since you have Git, you can use **GitHub Actions** to build your APK automatically in the cloud - no Android Studio needed!

### Step 1: Create a GitHub Repository

1. Go to https://github.com/new
2. Create a new repository (name it whatever you want, e.g., "app-lock")
3. Don't initialize with README (we already have one)
4. Click "Create repository"

### Step 2: Push Your Code to GitHub

Open PowerShell in your project folder and run:

```powershell
cd "C:\Users\SIDDHESH\Desktop\PROJECTS\app lock"

# Add all files
git add .

# Commit
git commit -m "Initial commit - App Lock application"

# Add your GitHub repo (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/app-lock.git

# Push to GitHub
git push -u origin master
```

### Step 3: Get Your APK

1. Go to your GitHub repository page
2. Click on **"Actions"** tab at the top
3. You'll see a workflow running called "Build APK"
4. Wait 2-3 minutes for it to complete (green checkmark ✓)
5. Click on the completed workflow
6. Scroll down to **"Artifacts"** section
7. Download **"app-debug"** - this is your APK!

### Step 4: Install on Phone

1. Extract the downloaded zip file
2. Transfer `app-debug.apk` to your Samsung phone
3. Install it (enable "Install from unknown sources" if asked)
4. Done! 🎉

---

## 🔄 Making Changes

Whenever you want to rebuild:

```powershell
git add .
git commit -m "Your changes"
git push
```

GitHub Actions will automatically build a new APK for you!

---

## 📱 Alternative: Local Build (if you have Gradle)

If you have Gradle installed locally:

```powershell
cd "C:\Users\SIDDHESH\Desktop\PROJECTS\app lock"
.\gradlew assembleDebug
```

APK will be at: `app\build\outputs\apk\debug\app-debug.apk`

---

## ⚡ What I've Set Up

✅ `.gitignore` - Excludes build files from Git  
✅ GitHub Actions workflow - Automatic APK building  
✅ Gradle wrapper - Build system configuration  
✅ Complete project ready to push

---

**Need help?** Just let me know at any step!
