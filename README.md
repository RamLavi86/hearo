# hearo

Android hearing-screening app (pure-tone thresholds, digits-in-noise, word discrimination),
English and Hebrew. Screening aid, not a diagnostic device.

- `core/` – pure Kotlin/JVM test logic (threshold search, audiogram, classification scales,
  tone generation). No Android dependency; unit-tested.
- `app/` – Android app (Jetpack Compose). Included in the build only when an Android SDK is
  configured (`ANDROID_HOME` or `sdk.dir` in `local.properties`).
- `docs/` – research and product decisions. Start with `docs/decisions.md`.

```bash
./gradlew :core:test        # works without the Android SDK
./gradlew :app:assembleDebug  # needs the Android SDK
```

Status: `:core` is tested. `:app` has not been compiled yet.
