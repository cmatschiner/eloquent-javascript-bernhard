# Sprint 0 – Status & Testbericht

> **Sprint-Ziel:** Projekt-Setup, Architektur-Grundgerüst, Navigation, CI, erste getestete Logik.

## Gelieferte Artefakte
- **Android-Projekt** unter `app/` (Kotlin + Jetpack Compose, Gradle 8.11.1, AGP 8.7.3).
- **Architektur:** MVVM-Grundlage, Hilt (DI) eingebunden, Navigation Compose mit Bottom-Bar.
- **Screens (Platzhalter):** Aufnahme, Kalenderübersicht, Detail, Einstellungen.
- **Kernlogik + Tests:** `MeetingFileNamer` (Dateinomenklatur) mit Unit-Tests.
- **CI:** GitHub Actions (`.github/workflows/android-ci.yml`) – baut Debug-APK + führt Unit-Tests aus.
- **Version-Katalog:** `app/gradle/libs.versions.toml`.

## Konfiguration
| Einstellung | Wert |
|-------------|------|
| Sprache/UI-Toolkit | Kotlin / Jetpack Compose |
| minSdk | 31 (Android 12) |
| compileSdk / targetSdk | 35 (Android 15) – siehe Hinweis unten |
| applicationId | `at.matschiner.meetminutes` |
| Gradle / AGP / Kotlin | 8.11.1 / 8.7.3 / 2.0.21 |

> **Hinweis zur Ziel-SDK (Android 16):** `compile/targetSdk` stehen auf 35, weil AGP 8.7.x
> maximal Android 15 unterstützt. Die App **läuft** dank `minSdk 31` problemlos auf Android 16.
> Der Sprung auf `targetSdk 36` erfolgt zusammen mit einem AGP-Upgrade (≥ 8.9) als kleiner,
> separat testbarer Schritt – bewusst aus dem risikoreichen Sprint-0-Setup herausgehalten.

## Tests
- **Lokal (diese Umgebung):** Vollständiger Android-Build **nicht möglich**, da der
  Netzwerk-Proxy Googles Maven-Repo (`dl.google.com`) blockt (403). Verifiziert wurde:
  Gradle-Wrapper lauffähig (`./gradlew --version`), Settings & Version-Katalog parsen korrekt.
- **CI (GitHub Actions):** Maßgebliche Verifikation – installiert Android SDK, führt
  `testDebugUnitTest` und `assembleDebug` aus. **Definition of Done = CI grün.**

## Definition of Done (Sprint 0) — ✅ ERFÜLLT
- [x] CI-Pipeline grün (Build + Unit-Tests) — Run #2, Commit `968efc7`, `assembleDebug` + `testDebugUnitTest` erfolgreich
- [x] Navigations-Grundgerüst mit 4 Screens
- [x] DI (Hilt) eingebunden
- [x] Getestete Kernlogik (Nomenklatur)
- [x] Architektur-Entscheidungen dokumentiert (`05_Entscheidungen.md`)

> **Testergebnis:** GitHub Actions „Android CI" Run #2 = **success**. Debug-APK wird als
> Artefakt (`meetminutes-debug-apk`) hochgeladen. Sprint 0 ist abnahmebereit.

## Offene Spikes (in Folge-Sprints zu vertiefen)
- Whisper.cpp-Anbindung via JNI (Sprint 2)
- DOCX-Bibliothek auf Android evaluieren (Apache POI vs. docx4j, Sprint 3)
- AGP-Upgrade auf ≥ 8.9 für `targetSdk 36`

## Nächster Schritt
Nach grünem CI: **Abnahme durch Auftraggeber**, danach Start **Sprint 1 (Audioaufnahme & WAV)**.
