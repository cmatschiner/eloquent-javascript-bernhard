# Sprint 3 – Status & Testbericht

> **Sprint-Ziel:** Inhaltsanalyse des Transkripts und Erzeugung des Besprechungs-
> protokolls (Minutes of Meeting) als DOCX.

Umgesetzt in zwei Schritten: 3.1 DOCX-Pipeline (abhängigkeitsfrei), 3.2 Claude-API-Analyse + UI.

## Gelieferte Artefakte
**DOCX & Modell (3.1)**
- `MeetingAnalysis`-Modell: Zusammenfassung, Teilnehmer, TOP, Beschlüsse,
  Action Items (Verantwortlich/Frist/„mich betreffend"), Follow-ups, offene Punkte.
- `DocxBuilder`: **abhängigkeitsfreier** OOXML/ZIP-Writer (Überschriften, Absätze, Bullets, Tabellen).
- `MeetingMinutesComposer`: Ergebnisprotokoll-Layout gemäß `docs/04_MoM-Vorlage.md`.
- `MeetingStorage`: `.docx`-Protokoll mit identischer Nomenklatur.

**Analyse (3.2)**
- `AnalysisEngine`-Abstraktion; `ClaudeAnalysisEngine` (Anthropic Messages API,
  erzwungenes Tool-Use → strukturierte JSON-Ausgabe, ADR-02).
- `AnalysisPrompt` (System-Prompt + Tool-Schema), `AnalysisResponseParser` (org.json).
- `SettingsRepository`: **Anthropic-API-Key** + Analyse-Modell (Default `claude-sonnet-5`),
  Whisper-Modell, Sprache.
- UI: „Protokoll erstellen (DOCX)" nach der Transkription; **Einstellungen-Screen**
  (API-Key maskiert, Claude-Modell, **Whisper-Modell-Umschalter tiny/base/small**, Sprache).

## Tests
- **Unit-Tests (CI):** `DocxBuilderTest`, `MeetingMinutesComposerTest` (ZIP entpacken +
  Inhalt/Escaping prüfen), `AnalysisResponseParserTest` (JSON → Modell) – plus bestehende.
- **CI (GitHub Actions):** Run #9 (3.1) = success, **Run #11 (3.2) = success** (Commit `199099b`).
  Ein zwischenzeitlicher Fehler (`FlowRow` = experimentelle API) wurde gefunden und behoben.
- **Manuell am Gerät (durch Auftraggeber zu prüfen):**
  - [ ] API-Key in Einstellungen eintragen
  - [ ] Nach Transkription „Protokoll erstellen" → `.docx` wird erzeugt
  - [ ] DOCX öffnet in Word/Docs, Format & Inhalte (Beschlüsse, Action-Item-Tabelle) korrekt

## Definition of Done (Sprint 3)
- [x] Strukturierte Inhaltsanalyse (Claude API, Tool-Use)
- [x] DOCX-Protokoll im MoM-Format, gleiche Nomenklatur (getestet)
- [x] „mich betreffende" Aufgaben markiert (für späteren Google-Tasks-Sync)
- [x] Editierbarkeit: Protokoll ist als DOCX bearbeitbar (In-App-Editor folgt in Sprint 4)
- [x] CI grün (Build inkl. nativer Lib + Unit-Tests)
- [ ] **Manuelle Geräte-Abnahme** (API-Key, echte Analyse, DOCX-Öffnung)

## Hinweise / offene Punkte
- **API-Key** wird derzeit in SharedPreferences abgelegt; Verschlüsselung (Keystore) ist
  ein Härtungsschritt in Sprint 7.
- Lange Transkripte werden aktuell in einem Request analysiert; Map-Reduce für sehr lange
  Meetings kann später ergänzt werden.
- In-App-**Bearbeiten** des Protokolls (nur Protokoll) kommt in Sprint 4 (Kalenderübersicht).

## Nächster Schritt
Nach Abnahme: **Sprint 4 (Kalenderübersicht: anzeigen, aufrufen, Protokoll editieren, löschen)**.
