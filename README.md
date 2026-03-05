# TaskTracker (Android • Jetpack Compose)

TaskTracker is a simple habit/task tracker built with **Jetpack Compose** (Material 3).  
It supports creating, editing, deleting, and marking tasks as done. It also includes a **Progress & Insights** screen and the ability to **share progress as an image** using Android's share sheet.

## Features

- Create habits/tasks
- Edit habits/tasks (tap a task to open edit)
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

## Requirements

- Android Studio (Hedgehog or newer recommended)
- Android SDK 34
- Gradle / Kotlin plugins as configured in the project

---

## Setup & Run

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run on emulator or physical device (Android 7.0+)

---

## Data Persistence

Tasks are stored locally using **DataStore Preferences** in a JSON field (Gson).  
Closing and reopening the app keeps tasks saved.

> If you previously tested with sample data and want a clean start:
- Uninstall the app and install again, or
- Android Settings → Apps → TaskTracker → Storage → **Clear storage**

---

## Share Progress (Image)

The **Share Progress** button shares a PNG image using `FileProvider`.

### Required AndroidManifest provider

Make sure this exists inside `<application>`:

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

### Required file paths XML

Create this file:

`app/src/main/res/xml/file_paths.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <cache-path name="shared_images" path="." />
</paths>
```

### How it works

- Captures the current progress screen as a Bitmap (the screen is attached to the window)
- Saves it to `cacheDir/shared_images/`
- Shares it via the system share sheet (`ACTION_SEND`, `image/png`)

---

## Project Structure (Simplified)

- `model/Task.kt` – Task model + categories
- `data/TaskLocalDataStore.kt` – DataStore + Gson serialization
- `ui/MainActivity.kt` – Main UI + navigation
- `ui/RoutineItem.kt` – Task row UI
- `ui/ProgressScreen.kt` – Progress UI + share button
- `ui/ShareImage.kt` – Save bitmap and share using FileProvider
- `ui/ContextExt.kt` – helper to get Activity from Context
- `ui/ComposeViewCapture.kt` – captures root view into Bitmap

---

## Troubleshooting

### Share button does nothing / no share sheet appears
- Make sure you have apps installed that can receive images (e.g., Photos, Gmail, WhatsApp)
- Verify `FileProvider` authority matches the code
- Verify `res/xml/file_paths.xml` exists

### App crashes when sharing
- Usually caused by a mismatched FileProvider authority or missing `file_paths.xml`
- Check Logcat for `FileProvider` or `TaskTrackerShare` errors

---

## License

MIT (or your preferred license).