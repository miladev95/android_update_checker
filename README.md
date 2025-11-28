# Update Checker

A lightweight Android library that checks a remote JSON endpoint for the latest app version and shows an update dialog when a newer version is available.

This repository contains a demonstration app plus the update-checker implementation and a comprehensive test suite.

## Features

- Fetch version info from a JSON URL
- Compare server `versionCode` with current app `versionCode`
- Show an update dialog with release notes
- Support for force updates (non-dismissible dialog)
- Callbacks for update / no-update / error events
- Uses Kotlin Coroutines for asynchronous work

## Project layout

- `app/src/main/java/ir/zodiacgroup/updatechecker/` – main library code and demo activity
  - `UpdateChecker.kt` – main API
  - `models/VersionInfo.kt` – data model
  - `network/UpdateClient.kt` – HTTP + JSON parsing
  - `ui/UpdateDialog.kt` – dialog UI
- `app/src/test/resources/` – sample JSON files used by unit tests
- `app/src/test/` – unit tests
- `app/src/androidTest/` – instrumented / integration tests
- `QUICKSTART.md`, `USAGE.md`, `API.md`, `INTEGRATION.md` – documentation files

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

## Running tests

Unit tests:

```bash
./gradlew test
```

Instrumented tests (require connected device/emulator):

```bash
./gradlew connectedAndroidTest
```

There is a test runner script at `run_tests.sh` that runs unit tests and describes how to run instrumented tests.

## Test resources

Sample JSON files used by tests are located at `app/src/test/resources/`:
- `sample_version.json` (main test case)
- `force_update_version.json`
- `minimal_version.json`
- `invalid_missing_code.json`

## Notes & tips

- Minimum supported SDK in the demo is 24.
- The library automatically adds `INTERNET` permission in the demo app's manifest.
- For production, host your JSON via HTTPS and keep the `versionCode` as an integer you increment on release.

## Files to review

- `app/src/main/java/ir/zodiacgroup/updatechecker/UpdateChecker.kt`
- `app/src/main/java/ir/zodiacgroup/updatechecker/network/UpdateClient.kt`
- `app/src/main/java/ir/zodiacgroup/updatechecker/ui/UpdateDialog.kt`
- `app/src/main/java/ir/zodiacgroup/updatechecker/models/VersionInfo.kt`