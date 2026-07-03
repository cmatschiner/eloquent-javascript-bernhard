package at.matschiner.meetminutes.analysis

/** Kontext für die Analyse (Besprechungs-Metadaten). */
data class AnalysisContext(
    val art: String,
    val thema: String,
    val date: String, // JJJJ-MM-TT
    val language: String,
)

/**
 * Abstraktion über die Inhaltsanalyse. Default: Claude API (ADR-02).
 */
interface AnalysisEngine {
    /** Analysiert das Transkript und liefert das strukturierte Ergebnis. */
    suspend fun analyze(transcriptText: String, context: AnalysisContext): MeetingAnalysis
}
