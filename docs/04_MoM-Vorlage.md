# Entwurf: Besprechungsprotokoll / Minutes of Meeting (MoM)

> Dieser Entwurf definiert **Inhalt & Layout** des generierten `.docx` (und damit auch des PDF).
> Bitte abstimmen – danach beginnt die Programmierung.

---

## Layout-Aufbau (Reihenfolge der Abschnitte)

```
┌───────────────────────────────────────────────────────────┐
│                  BESPRECHUNGSPROTOKOLL                      │  ← Titel
│  {Art der Besprechung} – {Thema}                           │  ← Untertitel
├───────────────────────────────────────────────────────────┤
│ 1. Eckdaten (Tabelle)                                      │
│ 2. Teilnehmer / Abwesende                                  │
│ 3. Agenda / Tagesordnungspunkte (TOP)                      │
│ 4. Zusammenfassung (Executive Summary)                     │
│ 5. Besprechungsinhalt je TOP (Diskussion + Ergebnis)       │
│ 6. Beschlüsse / Entscheidungen                             │
│ 7. Action Items (Aufgabe | Verantwortlich | Frist | Status)│
│ 8. Follow-up-Termine                                       │
│ 9. Offene Punkte / Parkplatz                               │
│ 10. Anhang (Verweis Audio/Transkript) + Fußzeile          │
└───────────────────────────────────────────────────────────┘
```

---

## Konkreter Inhaltsentwurf (Beispiel)

### BESPRECHUNGSPROTOKOLL
**Meeting – Team-Meeting**

#### 1. Eckdaten
| Feld | Wert |
|------|------|
| Datum | 2026-06-29 |
| Uhrzeit | 10:00 – 10:45 |
| Art | Meeting |
| Thema | Team-Meeting |
| Ort / Format | Büro / Präsenz |
| Protokollführung | (automatisch erzeugt durch MeetMinutes) |
| Audiodatei | `2026-06-29_Meeting - Team-Meeting.wav` |

#### 2. Teilnehmer
| Name | Rolle | Anwesend |
|------|-------|----------|
| Sprecher 1 | — | ja |
| Sprecher 2 | — | ja |

*Abwesend/Entschuldigt:* —

> Namen können nach Diarisierung manuell zugeordnet/ergänzt werden.

#### 3. Agenda / Tagesordnungspunkte (TOP)
1. TOP 1 – …
2. TOP 2 – …

#### 4. Zusammenfassung (Executive Summary)
> Kompakte 3–6-Sätze-Zusammenfassung der wichtigsten Ergebnisse (KI-generiert).

#### 5. Besprechungsinhalt je TOP
**TOP 1 – {Titel}**
- *Diskussion:* …
- **Ergebnis:** …

**TOP 2 – {Titel}**
- *Diskussion:* …
- **Ergebnis:** …

#### 6. Beschlüsse / Entscheidungen
- **B1:** … *(fett hervorgehoben)*
- **B2:** …

#### 7. Action Items
| # | Aufgabe | Verantwortlich | Frist | Mich betr. | Status |
|---|---------|----------------|-------|:---------:|--------|
| 1 | … | Name | 2026-07-05 | ✓ | offen |
| 2 | … | Name | 2026-07-10 | – | offen |

> Spalte **„Mich betr."** steuert den Google-Tasks-Sync. **Frist** speist die Fälligkeit.

#### 8. Follow-up-Termine
| Titel | Datum/Uhrzeit | Teilnehmer | → Google Kalender |
|-------|---------------|-----------|:-----------------:|
| Folge-Meeting | 2026-07-12 09:00 | … | ✓ |

#### 9. Offene Punkte / Parkplatz
- …

#### 10. Anhang & Fußzeile
- Transkript: `2026-06-29_Meeting - Team-Meeting.md`
- Audio: `2026-06-29_Meeting - Team-Meeting.wav`
- *Erstellt mit MeetMinutes am 2026-06-29. KI-generiert – bitte inhaltlich prüfen.*

---

## Gestaltungsrichtlinien (DOCX/PDF)
- Klare Überschriftenhierarchie (Heading 1/2/3 → Navigations-/Inhaltsverzeichnis möglich).
- Beschlüsse **fett**; Action Items & Termine als **Tabellen** (gut maschinen- & menschenlesbar).
- Dezente Kopf-/Fußzeile mit Thema + Seitenzahl.
- Einheitliche Schrift (z. B. Calibri/Arial 11), genügend Weißraum.

## Offene Punkte zur Vorlage (bitte entscheiden)
1. **Tonalität:** Ergebnisprotokoll (knapp, ergebnisorientiert) ✔ empfohlen – oder Verlaufsprotokoll (ausführlich)?
2. **Sprechernamen:** automatisch „Sprecher 1/2" + manuelle Benennung – ok?
3. **Pflichtfelder:** Sind Uhrzeit/Ort optional (falls beim Aufnehmen nicht erfasst)?
4. **Logo/Branding** im Kopf erwünscht?
5. **Sprache des Protokolls:** immer Deutsch, oder Sprache der Aufnahme?
