# Pflichtenheft – Meeting-Recorder App ("MeetMinutes")

> **Status:** Entwurf v0.1 – zur Abstimmung
> **Datum:** 2026-06-29
> **Plattform:** Android (Ziel: Android 16, abwärtskompatibel bis Android 9 / API 28)
> **Dokumenttyp:** Pflichtenheft (basierend auf den fachlichen Anforderungen des Auftraggebers)

---

## 1. Zielsetzung & Vision

Eine native Android-App, die Besprechungen über das Mikrofon **aufnimmt**, **transkribiert**,
den **Inhalt analysiert** und drei Artefakte erzeugt und ablegt:

1. **Audioaufnahme** als `.wav`
2. **Reines Transkript** als `.md`
3. **Besprechungsprotokoll** ("Minutes of Meeting") als `.docx`

Ergänzend bietet die App eine **Kalenderübersicht** zur Verwaltung der Artefakte,
einen **PDF-Export** der Protokolle und eine **Synchronisation** mit
**Google Kalender** (Follow-up-Termine) und **Google Tasks** (mich betreffende Aufgaben).

**Leitgedanke:** Aus einem gesprochenen Meeting wird ohne manuelle Nacharbeit
ein professionelles, durchsuchbares, teilbares Protokoll.

---

## 2. Stakeholder & Nutzungskontext

| Rolle | Beschreibung |
|-------|--------------|
| Primärnutzer | Einzelperson (Auftraggeber), nimmt eigene Meetings/Telefonate auf |
| Datenhoheit | Aufnahmen sind vertraulich → Datenschutz ist zentrales Designkriterium |
| Geräte | Android-Smartphone, Version 16 und älter (Mindest-API noch abzustimmen) |
| Konnektivität | Teils offline (Aufnahme), online für KI-Analyse & Google-Sync |

---

## 3. Dateinomenklatur (verbindlich)

Alle drei Dateien einer Besprechung verwenden **denselben Basisnamen**:

```
{Datum}_{Art der Besprechung} - {Thema}.{ext}
```

- **Datum:** `JJJJ-MM-TT` (ISO 8601), z. B. `2026-06-29`
- **Art:** z. B. `Meeting`, `Telefonat`, `Workshop`, `Jour-fixe`
- **Thema:** z. B. `Team-Meeting`, `Telefonat mit Firma X`

**Beispiel:**
```
2026-06-29_Meeting - Team-Meeting.wav
2026-06-29_Meeting - Team-Meeting.md
2026-06-29_Meeting - Team-Meeting.docx
```

**Regeln:**
- Bei Namenskollision am selben Tag/Art/Thema wird ein Zähler angehängt: `… (2)`.
- Für das Dateisystem unzulässige Zeichen (`/ \ : * ? " < > |`) werden sicher ersetzt;
  der im Protokoll **angezeigte** Titel bleibt im Original erhalten (Mapping in DB).

---

## 4. Funktionale Anforderungen (FA)

Priorisierung nach **MoSCoW** (M = Must, S = Should, C = Could, W = Won't/später).

### 4.1 Aufnahme
| ID | Anforderung | Prio |
|----|-------------|------|
| FA-01 | Audioaufnahme über das Mikrofon starten/pausieren/stoppen | M |
| FA-02 | Aufnahme als **WAV** (PCM, 16 kHz/16 bit Mono als Default, konfigurierbar) speichern | M |
| FA-03 | Vor der Aufnahme: Eingabe/Bestätigung von **Datum, Art, Thema** | M |
| FA-04 | Aufnahme im Hintergrund / bei gesperrtem Display fortführen (Foreground-Service) | M |
| FA-05 | Pegelanzeige & Aufnahmedauer live anzeigen | S |
| FA-06 | Schutz vor Datenverlust bei Absturz/Anruf (Auto-Save, Wiederaufnahme) | S |

### 4.2 Transkription
| ID | Anforderung | Prio |
|----|-------------|------|
| FA-10 | Automatische Transkription der Aufnahme (Sprache: **Deutsch**, optional weitere) | M |
| FA-11 | Transkript als **Markdown (.md)** speichern (reiner Text, Zeitmarken optional) | M |
| FA-12 | Sprecher-Trennung (Diarisierung) "Sprecher 1/2 …", später benennbar | S |
| FA-13 | Manuelle Korrektur des Transkripts vor der Analyse möglich | C |

### 4.3 Inhaltsanalyse & Protokollerstellung
| ID | Anforderung | Prio |
|----|-------------|------|
| FA-20 | KI-gestützte Analyse: Zusammenfassung, Beschlüsse, **To-Dos (Verantwortlich + Frist)**, Follow-up-Termine, offene Punkte | M |
| FA-21 | Erzeugung des **Besprechungsprotokolls (.docx)** im standardisierten MoM-Format (siehe `04_MoM-Vorlage.md`) | M |
| FA-22 | Erkennung "mich betreffender" Aufgaben (für Google-Tasks-Sync) | M |
| FA-23 | Editierbarkeit des generierten Protokolls in der App | M |

### 4.4 Ablage & Kalenderübersicht
| ID | Anforderung | Prio |
|----|-------------|------|
| FA-30 | Kalenderübersicht: Besprechungen pro Tag/Monat anzeigen | M |
| FA-31 | Pro Eintrag: Protokoll, Transkript, Audio **aufrufen** | M |
| FA-32 | **Editierbar: nur Besprechungsprotokoll** (Audio & Transkript schreibgeschützt) | M |
| FA-33 | Einträge (inkl. zugehöriger Dateien) **löschen** | M |
| FA-34 | Suche/Filter nach Datum, Art, Thema | S |

### 4.5 Export & Teilen
| ID | Anforderung | Prio |
|----|-------------|------|
| FA-40 | **PDF-Export** des Besprechungsprotokolls | M |
| FA-41 | Teilen via Android Share-Sheet (PDF/DOCX/MD/WAV) | S |

### 4.6 Google-Synchronisation
| ID | Anforderung | Prio |
|----|-------------|------|
| FA-50 | OAuth-2.0-Anmeldung mit Google-Konto | M |
| FA-51 | **Google Kalender:** Follow-up-Termine als Events anlegen (mit Bestätigung) | M |
| FA-52 | **Google Tasks:** mich betreffende To-Dos als Aufgaben anlegen (Titel, Notiz, Fälligkeit) | M |
| FA-53 | Vor dem Sync: Review-Dialog (welche Termine/Tasks werden angelegt) | M |
| FA-54 | Doppelte Anlage vermeiden (Idempotenz über gespeicherte Referenz-IDs) | S |

---

## 5. Nicht-funktionale Anforderungen (NFA)

| ID | Kategorie | Anforderung |
|----|-----------|-------------|
| NFA-01 | Plattform | Läuft auf Android 16 und älter (Ziel-Mindest-API in Sprint 0 final festlegen) |
| NFA-02 | Datenschutz | Aufnahmen/Transkripte primär lokal; Cloud-Verarbeitung nur nach expliziter Zustimmung; transparente Datenschutzerklärung |
| NFA-03 | Sicherheit | OAuth-Tokens verschlüsselt (Android Keystore / EncryptedSharedPreferences); Dateien im app-spezifischen Speicher |
| NFA-04 | Sprache (UI) | Deutsch (primär), i18n-fähig |
| NFA-05 | Performance | Transkription nutzbar; klare Fortschrittsanzeige; keine UI-Blockaden |
| NFA-06 | Robustheit | Kein Datenverlust bei App-Kill/Anruf während Aufnahme |
| NFA-07 | Barrierefreiheit | Kontraste, TalkBack-Labels, skalierbare Schrift |
| NFA-08 | Wartbarkeit | Saubere Architektur (MVVM + Clean), Testabdeckung der Kern-Logik |
| NFA-09 | Offline | Aufnahme & Wiedergabe offline; Analyse/Sync online |

---

## 6. Architektur & Technologie (Vorschlag – Teil der Abstimmung)

- **Sprache/Framework:** Kotlin + **Jetpack Compose** (Android-only → native ist performanter,
  Day-1-Zugriff auf Plattform-Features, volle Kontrolle). Alternative Flutter nur falls iOS später relevant wird.
- **Architektur:** MVVM + Clean Architecture, Dependency Injection (Hilt), Coroutines/Flow.
- **Persistenz:** Room (Metadaten/Index) + Dateien im App-Storage; `MediaStore`/SAF für Export.
- **Audio:** `AudioRecord`/`MediaRecorder` → WAV (PCM) via Foreground-Service.
- **Transkription (STT):** zwei Optionen (siehe `02_Recherche.md`):
  - **On-Device** (Whisper.cpp via JNI oder Vosk) → maximaler Datenschutz, offline.
  - **Cloud** (z. B. Whisper-/AssemblyAI-API) → höhere Genauigkeit + Diarisierung.
- **Inhaltsanalyse (LLM):** Cloud-LLM (z. B. **Claude API**) für Zusammenfassung & strukturierte To-Do-/Termin-Extraktion (JSON via Tool-Use). Optional on-device-LLM als Privacy-Variante.
- **DOCX:** Apache POI (XWPF) bzw. docx4j; **PDF:** Android `PdfDocument`/`PrintManager` oder Konvertierung aus dem Protokoll-Layout.
- **Google-Sync:** Google Identity Services (OAuth) + Calendar API + Tasks API.

> Die Wahl On-Device vs. Cloud (STT & LLM) ist die wichtigste offene Entscheidung – siehe Abstimmungsfragen.

---

## 7. Datenmodell (Auszug)

`Meeting` (Room-Entity): `id`, `datum`, `art`, `thema`, `basisDateiname`, `audioPfad`,
`transkriptPfad`, `protokollPfad`, `dauer`, `status` (AUFGENOMMEN→TRANSKRIBIERT→ANALYSIERT),
`erstelltAm`, `geaendertAm`.
`ActionItem`: `id`, `meetingId`, `beschreibung`, `verantwortlich`, `frist`, `michBetreffend`,
`googleTaskId?`, `googleEventId?`.

---

## 8. Abgrenzung / Nicht-Ziele

- **Keine** iOS-/Web-Version in v1 (Android-only).
- **Kein** Multi-User-/Team-Backend, keine geräteübergreifende Cloud-Sync (außer Google-Termine/Tasks).
- **Keine** Echtzeit-Live-Transkription während des Sprechens in v1 (Transkription nach Stopp) – kann später ergänzt werden.
- **Keine** automatische Aufnahme von Telefonanrufen über die Telefonleitung (nur Mikrofon/Raum).
- **Kein** automatisches Versenden von Protokollen ohne Nutzerfreigabe.

---

## 9. Annahmen, Risiken & Maßnahmen

| Risiko | Auswirkung | Maßnahme |
|--------|-----------|----------|
| STT-Genauigkeit bei Deutsch/Dialekt/Störgeräusch | Schlechtes Protokoll | Modellwahl evaluieren; manuelle Korrektur (FA-13) |
| Datenschutz bei Cloud-Verarbeitung | Vertraulichkeitsverlust | On-Device-Option; explizite Zustimmung; Verschlüsselung |
| Google API Quotas / OAuth-Verifizierung | Sync eingeschränkt | Frühe API-Freigabe; Review-Screen; Fehlerbehandlung |
| Android-Hintergrund-Restriktionen | Aufnahme bricht ab | Foreground-Service + korrekte Notifications |
| Rechtliches (Aufnahme von Gesprächen) | Compliance | Hinweis/Einwilligungs-Reminder in der App |

---

## 10. Abnahmekriterien (gesamt)

Das Projekt gilt als erfolgreich abgeschlossen, wenn:
1. Eine reale Aufnahme → WAV, MD, DOCX mit korrekter Nomenklatur erzeugt.
2. Das DOCX dem abgestimmten MoM-Format entspricht.
3. Kalenderübersicht: anzeigen, aufrufen, Protokoll editieren, löschen funktioniert.
4. PDF-Export ein korrektes Protokoll erzeugt.
5. Google-Kalender-Termin und Google-Task aus einem Protokoll nachweislich angelegt werden.
6. Alle Sprints ihre jeweilige "Definition of Done" bestanden haben (siehe `03_Sprintplan.md`).
