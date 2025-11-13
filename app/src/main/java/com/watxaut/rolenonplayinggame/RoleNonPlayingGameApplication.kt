package com.watxaut.rolenonplayinggame

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the Role Non-Playing Game
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection
 */
@HiltAndroidApp
class RoleNonPlayingGameApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // TODO: Initialize analytics, crash reporting, etc.
    }
}
