# TaskTracker (Android • Jetpack Compose)

TaskTracker is a simple habit/task tracker built with **Jetpack Compose** (Material 3).  
It supports creating, editing, deleting, and marking tasks as done. It also includes a **Progress & Insights** screen and the ability to **share progress as an image** using Android's share sheet.

## Design / UI Credit (Important)

**UI/visual design credit:** The layout and visual style are **not original**.  
This project recreates a design/mockup I found online for learning/practice purposes.  
If you know the original source/author, please open an issue/PR so proper attribution can be added.

## Features

- Create habits/tasks
- Edit habits/tasks
- Mark task as done (toggle)
- Delete task
- Data persistence using **DataStore Preferences + Gson**
- Progress screen with category bars and points
- Share progress as **PNG image** (FileProvider)

## Tech Stack

- Kotlin
- Jetpack Compose (Material 3)
- DataStore Preferences
- Gson
- minSdk **24**, targetSdk **34**

---

## Setup & Run

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run on emulator or physical device (Android 7.0+)

---

## Share Progress (Image)

The **Share Progress** button shares a PNG image using `FileProvider`.

### AndroidManifest provider

Inside `<application>`:

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

### `file_paths.xml`

Create:

`app/src/main/res/xml/file_paths.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <cache-path name="shared_images" path="." />
</paths>
```

---

## Where to upload the APK (so others can test)

You have a few good options:

### Option A — GitHub Releases (recommended if your code is on GitHub)
1. Build a release APK:
    - Android Studio: **Build → Generate Signed Bundle / APK → APK**
2. In GitHub:
    - Go to your repo → **Releases** → **Draft a new release**
    - Upload the `app-release.apk`
3. Share the Release link. People can download the APK from there.

### Option B — Firebase App Distribution (best tester experience)
Great for sharing to testers with install links and update notifications.
- Create a Firebase project → enable **App Distribution**
- Upload the APK/AAB
- Add tester emails (or invite link depending on your setup)

### Option C — Google Play Internal testing / Closed testing
Best if you want “real” Play Store install flow.
- Upload an AAB
- Create an internal/closed testing track
- Invite testers via email / link

### Option D — Direct file hosting (quick & simple)
- Google Drive / Dropbox / OneDrive
- Upload the APK and set sharing to “Anyone with the link”

> Tip: For safety, always tell testers to only install APKs from sources they trust.

---

## License

MIT (or your preferred license).