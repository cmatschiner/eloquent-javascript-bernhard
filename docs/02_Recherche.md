# Recherche-Ergebnisse (Internet-Auswertung)

> Grundlage für die technischen und fachlichen Entscheidungen. Quellen am Ende.

---

## 1. Minutes of Meeting / Besprechungsprotokoll – Standardaufbau

**International (MoM) – übliche Abschnitte:**
- Titel/Thema, Datum, Uhrzeit, Ort/Format
- Teilnehmer (mit Rolle) + Abwesende/Entschuldigte
- Tagesordnung (Agenda) / Tagesordnungspunkte (TOP)
- Zusammenfassung der Diskussion je TOP
- **Beschlüsse/Entscheidungen** (klar hervorgehoben, z. B. fett)
- **Action Items**: Aufgabe | Verantwortlicher | Frist (jede Aufgabe **ein** Owner + Deadline)
- Nächster Termin / Vertagung
- Optional: Unterschriften (Sitzungsleitung, Protokollführung)

**Best Practices:**
- Protokoll zeitnah (24–48 h) erstellen und teilen.
- Konsistentes Format → Verlässlichkeit.
- Outcomes (vereinbart/genehmigt/vertagt/abgelehnt) leicht auffindbar machen.
- Keine geteilte Verantwortung: ohne Owner passiert nichts, ohne Frist driftet es.

**Deutsche Konvention (Besprechungsprotokoll):**
- Kopfteil (Rahmendaten), Hauptteil (Beschlüsse/Aufgaben/Fristen/Verantwortliche), Anhang (Agenda/Anlagen).
- Aufgaben als **Tabelle: Aufgabe | Verantwortlicher | Frist**.
- Je TOP ein dokumentiertes **Ergebnis**.
- Unterscheidung Verlaufs- vs. **Ergebnisprotokoll** (für uns: Ergebnisprotokoll-Stil).

➡️ **Konsequenz:** Unsere `.docx`-Vorlage folgt dem Ergebnisprotokoll-Stil mit TOP-Struktur
und Action-Item-Tabelle (siehe `04_MoM-Vorlage.md`).

---

## 2. Spracherkennung (Speech-to-Text) auf Android

| Lösung | Modus | Deutsch | Genauigkeit | Diarisierung | Hinweise |
|--------|-------|---------|-------------|--------------|----------|
| **Vosk** | On-Device, offline | ja | mittel | begrenzt | sehr klein (~50 MB), streaming, CPU-freundlich, Open Source |
| **Whisper.cpp** | On-Device, offline | ja (99 Sprachen) | hoch | nein (extra nötig) | C/C++, läuft auf Mobilgeräten, höhere Last; 1–2 s Latenz bei Streaming |
| **Cloud-API** (Whisper-API, AssemblyAI o. ä.) | Online | ja | sehr hoch | ja (>98 %) | beste Qualität + Sprechertrennung, aber Daten verlassen das Gerät |

**Fazit:**
- **Maximaler Datenschutz/offline** → Whisper.cpp (bessere Qualität als Vosk) oder Vosk (leichter).
- **Beste Qualität + Sprechertrennung** → Cloud-API.
- Empfehlung: **Whisper.cpp on-device als Default**, Cloud als optionale "High-Quality"-Einstellung.
  (Finale Entscheidung mit Auftraggeber – Datenschutz vs. Genauigkeit.)

---

## 3. Inhaltsanalyse mit LLM

- Roh-Transkript → LLM erzeugt strukturierte Ausgabe: Zusammenfassung, Beschlüsse,
  **Action Items mit Owner + Frist**, Follow-up-Termine, offene Fragen.
- Bewährt: **Map-Reduce** für lange Transkripte (15-Min-Chunks zusammenfassen, dann finale Reduktion).
- **Strukturierte Extraktion** (JSON/Tool-Use) für maschinenlesbare To-Dos/Termine → speist Google Tasks/Calendar.
- Sprechertrennung (Diarisierung) verbessert "wer hat was zugesagt".
- **Claude API** eignet sich für Zusammenfassung + Action-Item-Tracking; strukturierte Outputs via Tool-Use.

---

## 4. Tech-Stack Android

- **Jetpack Compose (Kotlin)** ist 2026 produktionsreif; für **Android-only** die beste Wahl
  (Performance, native Plattform-Features, Code-Hoheit). Flutter nur bei späterem iOS-Bedarf sinnvoll.
- **PDF:** Jetpack PDF-Library / Android `PdfDocument` / `PrintManager`.
- **DOCX:** Apache POI (XWPF) oder docx4j (Bibliotheks-Footprint auf Android prüfen → Sprint 3 Spike).
- **Audio:** `AudioRecord`/`MediaRecorder` für WAV.

---

## 5. Google-Integration

- **OAuth 2.0** über Google Identity Services; Android-OAuth-Client erforderlich.
  Möglichst **eng gefasste Scopes** (Nutzer gewähren limitierte, klar beschriebene Zugriffe eher).
- **Google Tasks API:** Ressourcen *task lists* + *tasks* (Titel, Notiz, Fälligkeit, Status).
  Aufgaben mit Datum erscheinen automatisch im Google Kalender.
- **Google Calendar API:** Events (Follow-up-Termine) anlegen.
- Wichtig: OAuth-Consent-Screen/Verifizierung früh beantragen (Vorlaufzeit einplanen).

---

## Quellen

**Minutes of Meeting / Protokoll**
- [Fellow – Meeting Minutes Examples & Best Practices](https://fellow.ai/blog/meeting-minutes-example-and-best-practices/)
- [BoardEffect – Board meeting minutes 101](https://www.boardeffect.com/blog/board-meeting-minutes-template-best-practices/)
- [Indeed – How To Write Meeting Minutes](https://www.indeed.com/career-advice/career-development/meeting-minutes-template-examples)
- [SpeakNotes – Best Practices 2026](https://speaknotes.io/blog/best-practices-for-meeting-minutes)
- [büro-kaizen – Besprechungsprotokoll Aufbau](https://www.buero-kaizen.de/besprechungsprotokoll/)
- [büro-kaizen – Ergebnisprotokoll](https://www.buero-kaizen.de/ergebnis-protokoll/)
- [Asana – Protokoll Arten & Aufbau](https://asana.com/de/resources/how-to-write-protocols)

**Speech-to-Text**
- [Vosk – Offline speech recognition on Android](https://alphacephei.com/vosk/android)
- [Vosk API (GitHub)](https://github.com/alphacep/vosk-api)
- [whisper.cpp (GitHub)](https://github.com/ggml-org/whisper.cpp)
- [whisper_android (GitHub)](https://github.com/vilassn/whisper_android)
- [Vosk vs Whisper Local 2026](https://www.sinologic.net/en/2026-05/vosk-vs-whisper-local-the-ultimate-2026-guide-to-self-hosted-speech-recognition-stt.html)
- [Running Transcription Models on the Edge](https://www.ionio.ai/blog/running-transcription-models-on-the-edge-a-practical-guide-for-devices)

**LLM-Analyse**
- [AssemblyAI – Summarize meetings with LLMs](https://www.assemblyai.com/blog/summarize-meetings-llms-python)
- [Claude for Meeting Summarisation & Action Items](https://claudeimplementation.com/blog/claude-meeting-summarisation-tutorial)
- [Speaker Diarization workflow](https://www.mindstudio.ai/blog/speaker-diarization-ai-transcription-workflow)

**Tech-Stack & Google APIs**
- [Jetpack Compose vs Flutter 2026](https://ecorpit.com/jetpack-compose-vs-flutter/)
- [Android Jetpack PDF](https://developer.android.com/jetpack/androidx/releases/pdf)
- [Google – OAuth 2.0 for Google APIs](https://developers.google.com/identity/protocols/oauth2)
- [Google Tasks API (REST)](https://developers.google.com/workspace/tasks/reference/rest)
- [Google Tasks API – Scopes](https://developers.google.com/workspace/tasks/auth)
