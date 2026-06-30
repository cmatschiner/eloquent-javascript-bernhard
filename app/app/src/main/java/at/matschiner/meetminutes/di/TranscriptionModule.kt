package at.matschiner.meetminutes.di

import at.matschiner.meetminutes.transcription.TranscriptionEngine
import at.matschiner.meetminutes.transcription.WhisperEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Bindet die Default-Transkriptions-Engine (On-Device Whisper). */
@Module
@InstallIn(SingletonComponent::class)
abstract class TranscriptionModule {

    @Binds
    @Singleton
    abstract fun bindTranscriptionEngine(impl: WhisperEngine): TranscriptionEngine
}
