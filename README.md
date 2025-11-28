# Update Checker

A lightweight Android library that checks a remote JSON endpoint for the latest app version and shows an update dialog when a newer version is available.

## Features

- Fetch version info from a JSON URL
- Compare server `versionCode` with current app `versionCode`
- Show an update dialog with release notes
- Support for force updates (non-dismissible dialog)
- Callbacks for update / no-update / error events
- Uses Kotlin Coroutines for asynchronous work

## Project layout

- `app/src/main/java/ca.miladev95/updatechecker/` – main library code and demo activity
  - `UpdateChecker.kt` – main API
  - `models/VersionInfo.kt` – data model
  - `network/UpdateClient.kt` – HTTP + JSON parsing
  - `ui/UpdateDialog.kt` – dialog UI
- `app/src/test/resources/` – sample JSON files used by unit tests
- `app/src/test/` – unit tests
- `app/src/androidTest/` – instrumented / integration tests

## Quick start

1. Host a JSON file (example `version.json`) on a web server or CDN with this structure:

```json
{
  "versionCode": 2,
  "versionName": "1.0.1",
  "updateUrl": "https://example.com/app-v1.0.1.apk",
  "releaseNotes": "Bug fixes and improvements",
  "forceUpdate": false
}
```

2. In your Activity (or on app start), call:

```kotlin
UpdateChecker(this)
    .checkAndShowDialog("https://your-server.com/version.json")
```

3. Optionally add callbacks:

```kotlin
UpdateChecker(this)
    .setOnUpdateAvailable { info -> /* handle */ }
    .setOnNoUpdateAvailable { /* handle */ }
    .setOnError { error -> /* handle */ }
    .check("https://your-server.com/version.json")
```

4. Using the library via JitPack

Kotlin DSL (consumer):
```kotlin
repositories {
  maven { url = uri("https://jitpack.io") }
}

dependencies {
  implementation("com.github.miladev95:update_checker:v1.0.0")
}
```

Groovy DSL (consumer):
```groovy
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.miladev95:update_checker:v1.0.0'
}
```

## Running tests

Unit tests:

```bash
./gradlew test
```

Instrumented tests (require connected device/emulator):

```bash
./gradlew connectedAndroidTest
```

## Test resources

Sample JSON files used by tests are located at `app/src/test/resources/`:
- `sample_version.json` (main test case)
- `force_update_version.json`
- `minimal_version.json`
- `invalid_missing_code.json`

## Notes & tips

- Minimum supported SDK in the demo is 24.
- For production, host your JSON via HTTPS and keep the `versionCode` as an integer you increment on release.

## Files to review

- `app/src/main/java/ca.miladev95/updatechecker/UpdateChecker.kt`
- `app/src/main/java/ca.miladev95/updatechecker/network/UpdateClient.kt`
- `app/src/main/java/ca.miladev95/updatechecker/ui/UpdateDialog.kt`
- `app/src/main/java/ca.miladev95/updatechecker/models/VersionInfo.kt`
