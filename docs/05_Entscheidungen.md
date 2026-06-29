# Architektur-Entscheidungen (ADR-Log)

> Festgehaltene Entscheidungen aus der Abstimmung mit dem Auftraggeber (2026-06-29).
> Diese sind die verbindliche Grundlage für Sprint 0 ff.

| # | Thema | Entscheidung | Begründung / Konsequenz |
|---|-------|--------------|--------------------------|
| ADR-01 | **Transkription (STT)** | **On-Device (Whisper.cpp) als Default, Cloud-API optional zuschaltbar** | Datenschutz zuerst (Aufnahmen bleiben lokal); Cloud als „High-Quality"-Option inkl. Sprechertrennung in den Einstellungen. Abstraktion über STT-Interface, damit beide Engines austauschbar sind. |
| ADR-02 | **Inhaltsanalyse (LLM)** | **Cloud-LLM (Claude API)** | Beste Qualität bei Zusammenfassung & strukturierter To-Do-/Termin-Extraktion. Transkript wird (nach expliziter Zustimmung) an die API gesendet. API-Key & Kostenhinweis in Einstellungen; Hinweis im Datenschutz-Onboarding. |
| ADR-03 | **Tech-Stack** | **Kotlin + Jetpack Compose (native, Android-only)** | Beste Performance & Plattform-Features; Min/Target-SDK in Sprint 0 final. MVVM + Clean Architecture, Hilt, Room, Coroutines/Flow. |
| ADR-04 | **Protokoll-Stil** | **Ergebnisprotokoll** | Knapp, ergebnisorientiert: Beschlüsse, Action Items, Termine im Fokus (siehe `04_MoM-Vorlage.md`). |

## Daraus abgeleitete Defaults (MoM-Vorlage, vorbehaltlich Feedback)
- **Sprechernamen:** automatisch „Sprecher 1/2…", manuell benennbar (Cloud-STT liefert Diarisierung; On-Device ggf. ohne).
- **Pflichtfelder:** Datum, Art, Thema = Pflicht. Uhrzeit/Ort = optional (leer/„—" falls nicht erfasst).
- **Branding/Logo:** vorerst kein Logo (kann später als Einstellung ergänzt werden).
- **Protokoll-Sprache:** Deutsch (Default), spätere Option „Sprache der Aufnahme".

## Datenschutz-Hinweis (Konsequenz aus ADR-01/02)
- Audio + Transkript: standardmäßig **lokal**.
- Versand an Cloud-LLM (ADR-02) bzw. Cloud-STT (ADR-01, optional) erfolgt **nur nach expliziter Nutzer-Zustimmung** pro Funktion; transparente Aufklärung im Onboarding und in den Einstellungen.
