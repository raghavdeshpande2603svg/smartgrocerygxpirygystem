Smart Grocery Android APK Project

This folder contains an isolated Android app project that wraps the current dashboard page as a mobile WebView app.

To build:
1. Open a terminal in this folder.
2. Run: gradlew.bat assembleRelease
3. APK output will appear in app/build/outputs/apk/release/

Note:
- This is a wrapper app for the existing webpage and does not include a full Android Studio setup badge/launcher asset set.
- The web content should be placed in app/src/main/assets/index.html for a complete local web app experience.
