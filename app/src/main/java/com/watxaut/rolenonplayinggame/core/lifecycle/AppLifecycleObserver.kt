package com.watxaut.rolenonplayinggame.core.lifecycle

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Observes app lifecycle to track when app goes to background/foreground
 * Used for offline simulation tracking
 */
@Singleton
class AppLifecycleObserver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val offlineSimulationManager: OfflineSimulationManager
) : DefaultLifecycleObserver {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isInForeground = false

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)

        if (!isInForeground) {
            isInForeground = true
            onAppForegrounded()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)

        if (isInForeground) {
            isInForeground = false
            onAppBackgrounded()
        }
    }

    /**
     * Called when app goes to background
     * Stores the current timestamp for the logged-in user
     */
    private fun onAppBackgrounded() {
        offlineSimulationManager.recordAppBackgrounded()
    }

    /**
     * Called when app comes to foreground
     * Checks if offline simulation is needed based on last background time
     */
    private fun onAppForegrounded() {
        scope.launch {
            // Check if offline simulation should run
            offlineSimulationManager.checkAndRunOfflineSimulation()
        }
    }
}
