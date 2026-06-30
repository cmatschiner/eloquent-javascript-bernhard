# Sprint 2 – Status & Testbericht

> **Sprint-Ziel:** Transkription der Aufnahme (Deutsch) und Erzeugung des reinen
> Transkripts als Markdown (`.md`) mit identischer Nomenklatur.

Umgesetzt in zwei verifizierbaren Schritten (2.1 Architektur/Pipeline, 2.2 native Engine).

## Gelieferte Artefakte
**Architektur & Pipeline (2.1)**
- `TranscriptionEngine`-Abstraktion (austauschbar On-Device/Cloud, ADR-01).
- `Transcript`/`TranscriptSegment`-Modell.
- `TranscriptMarkdownFormatter` – reines `.md`-Transkript (Kopf + Zeitmarken/Sprecher) – **unit-getestet**.
- `MeetingFileNamer.withExtension` (`.wav` → `.md`) + `MeetingStorage`-Transkript-Helfer.
- `WhisperModelManager` – Modell-Download beim 1. Start (atomar via `.part`).
- `SettingsRepository` – Modus / Modell (Default **small**) / Sprache.
- UI: „Transkribieren"-Aktion auf dem Aufnahme-Screen mit Fortschritt/Ergebnis/Fehler.

**Native On-Device-Engine (2.2)**
- `CMake`/NDK-Setup (arm64-v8a), **whisper.cpp v1.7.4** via `FetchContent`.
- JNI-Brücke `whisper_jni.cpp` (init / transcribe / free).
- `WhisperNative` (Kotlin, `loadLibrary`-Guard → kein Absturz ohne Lib).
- `WavPcmReader` – WAV → normalisiertes Float-PCM (Stereo-Mittelung) – **unit-getestet**.
- `WhisperEngine` – Modell sicherstellen → Audio lesen → native Inferenz → Segmente.

## Tests
- **Unit-Tests (CI):** `TranscriptMarkdownFormatterTest`, `MeetingFileNamerExtensionTest`,
  `WhisperModelTest`, `WavPcmReaderTest` (PCM16→Float mono/stereo) – plus bestehende Tests.
- **CI (GitHub Actions):**
  - Run #4 = success (Sprint 2.1)
  - **Run #5 = success (Sprint 2.2)** – nativer whisper.cpp-Build kompiliert (Commit `6a18298`).
- **Manuell am Gerät (durch Auftraggeber zu prüfen):**
  - [ ] Erste Transkription lädt das Modell (~466 MB, einmalig) und erzeugt ein `.md`
  - [ ] `.md` trägt denselben Basisnamen wie die `.wav` und enthält deutschen Text
  - [ ] Qualität/Geschwindigkeit der deutschen Erkennung akzeptabel

## Definition of Done (Sprint 2)
- [x] `.md`-Transkript-Pipeline + Formatter (getestet)
- [x] Gleiche Nomenklatur wie WAV; Verknüpfung im Speicher
- [x] On-Device-Whisper-Engine implementiert (native, ADR-01)
- [x] CI grün (Build inkl. nativer Bibliothek + Unit-Tests)
- [ ] **Manuelle Geräte-Abnahme** (Modell-Download, echte deutsche Transkription)

> **Wichtig:** Ob die native Inferenz auf dem Gerät korrekt deutsch transkribiert,
> kann die CI **nicht** prüfen (kein Emulator-Audio, kein arm64-Lauf). Das ist die
> entscheidende manuelle Abnahme. Der Modell-Download (~466 MB) erfolgt beim ersten
> Transkribieren und benötigt WLAN.

## Bekannte Einschränkungen / offene Punkte
- Aktuell **nur arm64-v8a** (moderne Geräte). Weitere ABIs bei Bedarf ergänzbar.
- Sprecher-Diarisierung (Sprecher 1/2) noch nicht aktiv (Whisper liefert standardmäßig keine);
  Felder im Modell vorhanden, Anbindung optional später.
- Cloud-Engine als Option (ADR-01) noch nicht implementiert – On-Device ist Default.

## Nächster Schritt
Nach Geräte-Abnahme: **Sprint 3 (Inhaltsanalyse via Claude API + Protokoll als DOCX)**.
