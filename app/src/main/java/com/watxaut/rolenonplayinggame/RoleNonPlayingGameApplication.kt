package com.watxaut.rolenonplayinggame

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.watxaut.rolenonplayinggame.core.lifecycle.AppLifecycleObserver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class for the Role Non-Playing Game
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection
 */
@HiltAndroidApp
class RoleNonPlayingGameApplication : Application() {

    @Inject
    lateinit var appLifecycleObserver: AppLifecycleObserver

    override fun onCreate() {
        super.onCreate()

        // Register lifecycle observer for offline simulation
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)

        // TODO: Initialize analytics, crash reporting, etc.
    }
}
