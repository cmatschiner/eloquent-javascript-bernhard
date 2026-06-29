# MeetMinutes (Android)

Meeting-Recorder-App: nimmt Besprechungen auf, transkribiert sie, analysiert den Inhalt
und erzeugt **WAV** (Audio), **MD** (Transkript) und **DOCX** (Besprechungsprotokoll /
Minutes of Meeting). Siehe Planungsdokumente unter [`../docs/`](../docs).

## Tech-Stack
- **Kotlin + Jetpack Compose** (native Android)
- **MVVM + Clean Architecture**, Hilt (DI), Navigation Compose
- **minSdk 31** (Android 12), läuft bis Android 16
- Transkription: On-Device (Whisper.cpp) als Default, Cloud optional (ab Sprint 2)
- Inhaltsanalyse: Cloud-LLM / Claude API (ab Sprint 3)

> Hinweis zur SDK-Version: `compileSdk`/`targetSdk` stehen aktuell auf **35** (Android 15),
> kompatibel mit AGP 8.7.x. Der Sprung auf 36 (Android 16) erfolgt zusammen mit AGP ≥ 8.9
> in einem späteren, risikoarmen Schritt. Die App **läuft** bereits auf Android 16.

## Projektstruktur
```
app/
├─ app/                      # Application-Modul (:app)
│  └─ src/main/java/at/matschiner/meetminutes/
│     ├─ core/naming/        # Dateinomenklatur (getestet)
│     ├─ ui/navigation/      # Navigation + Bottom-Bar
│     ├─ ui/screens/         # Aufnahme / Übersicht / Detail / Einstellungen
│     └─ ui/theme/           # Material-3-Theme
├─ gradle/libs.versions.toml # Version-Katalog
└─ settings.gradle.kts
```

## Build & Test
```bash
cd app
./gradlew testDebugUnitTest   # Unit-Tests (u. a. MeetingFileNamer)
./gradlew assembleDebug       # Debug-APK
```
Benötigt ein installiertes Android SDK (`local.properties` mit `sdk.dir=...` oder `ANDROID_HOME`).
CI (GitHub Actions) baut und testet automatisch – siehe `.github/workflows/android-ci.yml`.

## Dateinomenklatur
```
{Datum}_{Art der Besprechung} - {Thema}.{ext}
# Beispiel: 2026-06-29_Meeting - Team-Meeting.wav
```
