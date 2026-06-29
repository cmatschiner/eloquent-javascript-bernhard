package at.matschiner.meetminutes

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Application-Klasse – Einstiegspunkt für Dependency Injection (Hilt). */
@HiltAndroidApp
class MeetMinutesApp : Application()
