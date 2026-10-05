# SendReady

A private Android video-compression project using Kotlin, Jetpack Compose, and Media3. The source implements media selection, a compression queue, foreground-service progress, history, settings, and a sharing flow.

## Architecture

- `core/compression/`: Media3 transformation, progress tracking, service/notifications.
- `core/domain/`: presets, metadata, queue/state models, estimates, repository interfaces.
- `core/data/`: Room history, DataStore settings, media and queue repositories.
- `feature/`: home, compression, progress, finished, history, and settings screens.
- Hilt supplies dependencies; coroutine flows carry state into Compose.

## Build and inspect

Open the project in Android Studio with an Android SDK compatible with compileSdk 36 and a JDK 17 toolchain. The minimum supported Android API is 26.

```sh
./gradlew assembleDebug testDebugUnitTest lintDebug
```

Windows: `gradlew.bat assembleDebug testDebugUnitTest lintDebug`. No signed production build or app-store publication is claimed. Use test media, then verify exported playback, sizes, cancellation, background execution, permissions, and sharing on physical devices.

## Current status

The old Phase 2 README understated the implementation: later commits add the media queue, compression pipeline, history, and UX. The latest source is an in-development prototype. Existing tests are limited, and fresh Android build/device validation is still needed.

The project remains private. It is deliberately omitted from public profile links because a recruiter cannot inspect its implementation without access.
