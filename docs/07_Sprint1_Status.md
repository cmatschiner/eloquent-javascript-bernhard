# Sprint 1 – Status & Testbericht

> **Sprint-Ziel:** Audioaufnahme über das Mikrofon und Speicherung als WAV mit korrekter Nomenklatur.

## Gelieferte Artefakte
- **WAV-Header-Logik** (`core/audio/WavHeader.kt`): reine 44-Byte-RIFF/PCM-Erzeugung, **unit-getestet**.
- **Aufnahme** (`recording/RecordingService.kt`): Foreground-Service (Typ `microphone`),
  `AudioRecord` mit 16 kHz / Mono / 16 bit; Start/Pause/Resume/Stop.
- **Crash-Sicherheit:** WAV-Header wird ~jede Sekunde aktualisiert → Datei bleibt bei
  Abbruch/Kill abspielbar.
- **Speicherung** (`recording/MeetingStorage.kt`): kollisionsfreie WAV-Datei gemäß
  Nomenklatur `{Datum}_{Art} - {Thema}.wav` im app-spezifischen Speicher.
- **Zustands-Management** (`recording/RecordingStateHolder.kt`): Singleton + `StateFlow`.
- **UI** (`ui/screens/RecordScreen.kt` + `record/RecordViewModel.kt`): Eingabe Datum/Art/Thema
  (Pflichtfelder), Dauer- und Pegelanzeige, Start/Pause/Stopp, Laufzeit-Permissions
  (`RECORD_AUDIO`, `POST_NOTIFICATIONS`).

## Tests
- **Unit-Tests (CI):** `WavHeaderTest` (Magic Bytes, PCM-Felder, Chunk-/Datengrößen, Stereo)
  und weiterhin `MeetingFileNamerTest`.
- **CI (GitHub Actions):** „Android CI" **Run #3 = success** (`assembleDebug` + `testDebugUnitTest`),
  Commit `ef19618`. Debug-APK als Artefakt erzeugt.
- **Manuell auf Gerät (durch Auftraggeber zu prüfen):**
  - [ ] Aufnahme erzeugt abspielbare WAV mit korrektem Namen im App-Ordner
        (`Android/data/at.matschiner.meetminutes/files/recordings/`)
  - [ ] Aufnahme läuft bei gesperrtem Display weiter (Notification sichtbar)
  - [ ] Pause/Fortsetzen funktioniert; Anruf/App-Wechsel verliert keine Daten
  - [ ] Pegel- und Dauer-Anzeige aktualisieren sich live

## Definition of Done (Sprint 1)
- [x] WAV korrekt benannt & im App-Storage; Header-Logik getestet
- [x] Start/Pause/Stop über Foreground-Service implementiert
- [x] Nomenklatur inkl. Kollisions-Zähler (aus Sprint 0) angebunden
- [x] CI grün (Build + Unit-Tests)
- [ ] **Manuelle Geräte-Abnahme** (Audioqualität, Hintergrund, Anruf) durch Auftraggeber

> **Hinweis:** Die On-Device-Aufnahme/Hardware-Interaktion (AudioRecord, Foreground-Service)
> lässt sich nicht im CI testen – sie erfordert die manuelle Abnahme auf einem echten Gerät.
> Erst danach gilt Sprint 1 als vollständig abgenommen.

## Nächster Schritt
Nach Geräte-Abnahme: Start **Sprint 2 (Transkription & Markdown-Transkript)** –
Whisper.cpp on-device (Default) gemäß ADR-01.
