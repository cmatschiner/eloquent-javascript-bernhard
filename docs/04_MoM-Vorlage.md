# Verbindliche Vorlage: Besprechungsprotokoll (Minutes of Meeting)

> **Grundlage:** Die vom Auftraggeber gelieferte Vorlage
> „Projektmanagement-Meetingprotokoll" (Smartsheet-Layout, 13 Abschnitte).
> Die App erzeugt das `.docx` in **exakt dieser Abschnittsstruktur**.
> Beispiel-Datei: `MoM-Vorlage_App_13-Abschnitte.docx`.

## Grundsätze
- **Alle 13 Abschnitte** werden ausgegeben (formattreu zur Vorlage).
- Abschnitte, die aus einer **einzelnen Aufnahme** nicht ableitbar sind, erscheinen als
  **leeres, editierbares Gerüst** (manuell in der App ergänzbar) statt zu fehlen.
- Keine Übernahme der Smartsheet-Hinweistexte/des Haftungsausschlusses (nur die Struktur).
- Ergebnisprotokoll-Stil, Deutsch, Tabellen mit Kopfzeilen.

## Abschnitte (Reihenfolge & Felder)

| # | Abschnitt | Felder / Tabelle | Von KI befüllbar? |
|---|-----------|------------------|-------------------|
| 1 | **Meetingdetails** | Datum, Ort, Startzeit, Endzeit · Teilnehmer: Name, Rolle, Anwesend | Datum ✓; Ort/Zeiten teilweise; Teilnehmer via Diarisierung (Namen ggf. „Sprecher 1") |
| 2 | **Agenda** | TOP, Verantwortlich, Startzeit, Dauer | TOP ✓ (aus Gespräch abgeleitet); Zeiten meist leer |
| 3 | **Besprechung des vorherigen Meetings** | Zusammenfassung · Aktionspunkt, Verantwortlich, Status | nur wenn im Gespräch erwähnt, sonst leer |
| 4 | **Diskussionspunkte** | TOP, Anmerkungen zur Diskussion / Entscheidung | ✓ |
| 5 | **Aktionspunkte** | Aktionspunkt, Inhaber, Fälligkeit, **Mich betr.** | ✓ (Spalte „Mich betr." steuert Google-Tasks-Sync) |
| 6 | **Getroffene Entscheidungen** | Entscheidung inkl. Begründung | ✓ |
| 7 | **Risiken und Probleme** | Risiko/Problem, Milderungsplan | ✓ (falls genannt) |
| 8 | **Nächste Schritte** | Liste | ✓ |
| 9 | **Sonstige Themen** | Weiteres Element, Beschreibung, Ergebnis | ✓ (falls genannt) |
| 10 | **Bevorstehende Meilensteine** | Meilenstein, Termin | nur wenn genannt |
| 11 | **Fazit des Meetings** | Zusammenfassung · Datum/Uhrzeit/Ort nächstes Meeting | Zusammenfassung ✓; nächstes Meeting via Follow-up (→ Google Kalender) |
| 12 | **Anlagen oder Hilfsmaterialien** | Material/Link | automatisch: Verweis auf Transkript (.md) + Audio (.wav) |
| 13 | **Genehmigung und Unterschriften** | Name des Teilnehmers, Unterschrift (leer) | Namen aus Teilnehmern; Unterschrift manuell |

Fußzeile: „Erstellt mit MeetMinutes am {Datum}. KI-generiert – bitte inhaltlich prüfen."

## Konsequenzen für die App (Sprint-3-Überarbeitung)
- **Analyse-Datenmodell** (`MeetingAnalysis`) wird um die neuen Felder erweitert:
  Meetingdetails (Ort, Start/Ende), Agenda-Einträge (Verantwortlich/Zeit/Dauer),
  vorheriges Meeting, Diskussionspunkte, Risiken (Risiko/Milderung), nächste Schritte,
  sonstige Themen, Meilensteine, Fazit + nächstes Meeting, Genehmigung.
- **Claude-Prompt/Tool-Schema** wird entsprechend erweitert (strukturierte Extraktion).
- **`MeetingMinutesComposer`** erzeugt die 13 Abschnitte als tabellenbasiertes DOCX
  (Abschnittskopf farbig, Unterköpfe, leere Gerüste für nicht ableitbare Abschnitte).

## Offene Feinheiten (Defaults – anpassbar)
- Alle 13 Abschnitte immer anzeigen (leere als Gerüst). *(Alternative: leere ausblenden.)*
- Unterschriftenblock (Abschnitt 13) **enthalten**. *(Alternative: weglassen.)*
- Aktionspunkte-Tabelle mit Zusatzspalte **„Mich betr."** für den Google-Tasks-Sync.
