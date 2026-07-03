package at.matschiner.meetminutes.di

import at.matschiner.meetminutes.analysis.AnalysisEngine
import at.matschiner.meetminutes.analysis.ClaudeAnalysisEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Bindet die Inhaltsanalyse-Engine (Claude API, ADR-02). */
@Module
@InstallIn(SingletonComponent::class)
abstract class AnalysisModule {

    @Binds
    @Singleton
    abstract fun bindAnalysisEngine(impl: ClaudeAnalysisEngine): AnalysisEngine
}
