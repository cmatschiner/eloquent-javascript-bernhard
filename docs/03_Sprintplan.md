# Sprint-Plan & Projektmanagement

> **Vorgehen:** Iterativ-inkrementell (agil/Scrum-light). Jeder Sprint endet mit
> funktionsfähigem, **getestetem** Inkrement. **Erst nach erfolgreicher Testung (DoD erfüllt)
> wird der nächste Sprint begonnen.**

## Arbeitsweise & Qualitätssicherung (für alle Sprints)
- **Branch-Strategie:** Feature-Branches → PR → Merge nach Review.
- **Definition of Done (global):** Code kompiliert, Unit-Tests grün, manueller Testfall
  auf Gerät/Emulator bestanden, kurze Doku/Changelog aktualisiert, keine offenen Blocker.
- **Teststufen:** Unit-Tests (Logik), Instrumented-/UI-Tests (Compose), manuelle E2E-Checks je Sprint.
- **Sprint-Review:** Demo des Inkrements + Abnahme durch Auftraggeber **vor** Start des Folgesprints.
- **Definition of Ready:** Anforderung klar, abhängige Entscheidungen getroffen, Akzeptanzkriterien definiert.

---

## Sprint 0 – Setup, Architektur & Entscheidungen
**Ziele**
- Projekt-Setup (Kotlin, Jetpack Compose, Hilt, Room), Repo-Struktur, CI (Build + Tests).
- Min-/Target-SDK festlegen; Berechtigungskonzept (Mikrofon, Benachrichtigungen).
- Technische Spikes: STT-Variante (Whisper.cpp/Vosk/Cloud), DOCX-Lib auf Android.
- Navigations-Grundgerüst + leere Screens (Aufnahme, Übersicht, Detail, Einstellungen).

**Nicht-Ziele:** Keine echte Aufnahme/Transkription/Sync.

**Definition of Done / Test**
- App startet auf Emulator + realem Gerät; Navigation funktioniert.
- CI-Pipeline läuft grün; ADR-Dokumente (Architecture Decision Records) für STT/LLM/DOCX vorhanden.
- Entscheidungen On-Device vs. Cloud schriftlich fixiert.

---

## Sprint 1 – Audioaufnahme & WAV-Speicherung
**Ziele**
- Aufnahme starten/pausieren/stoppen via Foreground-Service.
- Speicherung als **WAV** mit korrekter **Nomenklatur** `{Datum}_{Art} - {Thema}.wav`.
- Eingabemaske für Datum/Art/Thema; Pegel- & Dauer-Anzeige.
- Crash-/Anruf-Sicherheit (Auto-Save).

**Nicht-Ziele:** Keine Transkription/Analyse.

**Definition of Done / Test**
- WAV ist abspielbar, korrekt benannt, im App-Storage.
- Aufnahme läuft bei gesperrtem Display weiter; Unterbrechung durch Anruf verliert keine Daten.
- Unit-Test Nomenklatur-Generator (inkl. Sonderzeichen & Kollisions-Zähler).

---

## Sprint 2 – Transkription & Markdown-Transkript
**Ziele**
- Integration der in Sprint 0 gewählten STT-Lösung (Deutsch).
- Erzeugung **`.md`-Transkript** mit gleicher Nomenklatur; optional Zeitmarken/Sprecher.
- Fortschrittsanzeige; Statuswechsel `AUFGENOMMEN → TRANSKRIBIERT`.

**Nicht-Ziele:** Keine inhaltliche Analyse/MoM.

**Definition of Done / Test**
- Aus einer Testaufnahme entsteht ein lesbares deutsches `.md`-Transkript.
- Fehlerfälle (kein Audio, Abbruch) sauber behandelt.
- Test: Transkript-Datei korrekt benannt & verknüpft (Room).

---

## Sprint 3 – Inhaltsanalyse & Protokoll (DOCX)
**Ziele**
- LLM-Analyse: Zusammenfassung, Beschlüsse, **Action Items (Owner+Frist)**, Follow-ups, offene Punkte
  → strukturierte (JSON-)Ausgabe.
- Erzeugung **`.docx`** im abgestimmten **MoM-Format** (`04_MoM-Vorlage.md`), gleiche Nomenklatur.
- Markierung "mich betreffender" Aufgaben.

**Nicht-Ziele:** Noch kein Google-Sync, kein PDF.

**Definition of Done / Test**
- DOCX öffnet fehlerfrei in Word/Docs und entspricht der Vorlage.
- Action-Item-Tabelle korrekt befüllt; strukturierte Daten in DB.
- Test mit ≥2 Beispiel-Transkripten (kurz/lang → Map-Reduce).

---

## Sprint 4 – Kalenderübersicht & Verwaltung
**Ziele**
- Kalender-/Listenansicht der Besprechungen (Tag/Monat).
- Detailansicht: Protokoll/Transkript/Audio **aufrufen**.
- **Editieren nur Protokoll**; Audio/Transkript schreibgeschützt.
- **Löschen** inkl. zugehöriger Dateien; Such-/Filterfunktion.

**Nicht-Ziele:** Kein Export/Sync.

**Definition of Done / Test**
- Anzeigen/Aufrufen/Editieren(Protokoll)/Löschen verifiziert; gelöschte Dateien tatsächlich entfernt.
- UI-Test der Navigation Übersicht → Detail → Edit.

---

## Sprint 5 – PDF-Export & Teilen
**Ziele**
- **PDF-Export** des Protokolls (Layout = MoM-Vorlage).
- Teilen via Android Share-Sheet (PDF/DOCX/MD/WAV).

**Nicht-Ziele:** Kein Google-Sync.

**Definition of Done / Test**
- PDF korrekt formatiert, teilbar, identische Inhalte wie DOCX.
- Test auf mehreren Geräten/Auflösungen.

---

## Sprint 6 – Google Calendar & Google Tasks Sync
**Ziele**
- OAuth-2.0-Login; sichere Token-Speicherung.
- **Follow-up-Termine → Google Kalender**; **mich betreffende To-Dos → Google Tasks**.
- **Review-Dialog** vor Anlage; Idempotenz (keine Duplikate).

**Nicht-Ziele:** Keine Zwei-Wege-Sync/Konfliktauflösung in v1.

**Definition of Done / Test**
- Nachweislich erzeugte Events/Tasks im echten Google-Konto.
- Fehler-/Offline-Handling; Token-Refresh funktioniert.
- Test: erneuter Sync legt keine Duplikate an.

---

## Sprint 7 – Härtung, E2E & Release-Vorbereitung
**Ziele**
- End-to-End-Test über den gesamten Flow (Aufnahme → Sync).
- Datenschutz-Feinschliff, Verschlüsselung, Barrierefreiheit, Performance.
- Onboarding/Einstellungen, Fehler-/Leerzustände, Release-Build & Signing.

**Definition of Done / Test**
- Vollständiger E2E-Durchlauf bestanden (Abnahmekriterien Pflichtenheft Kap. 10).
- Keine kritischen Bugs; Release-APK/AAB baubar.

---

## Übersicht / Reihenfolge & Abhängigkeiten

| Sprint | Thema | Voraussetzung |
|--------|-------|---------------|
| 0 | Setup & Entscheidungen | — |
| 1 | Aufnahme + WAV | 0 |
| 2 | Transkription + MD | 1 |
| 3 | Analyse + DOCX | 2 |
| 4 | Kalenderübersicht/Verwaltung | 3 |
| 5 | PDF-Export | 3/4 |
| 6 | Google-Sync | 3 |
| 7 | Härtung & Release | alle |

> Hinweis: Sprint-Längen werden nach Abstimmung der Rahmenbedingungen (Aufwand, Verfügbarkeit) festgelegt.
