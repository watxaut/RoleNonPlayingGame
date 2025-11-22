package com.watxaut.rolenonplayinggame.presentation.map

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.watxaut.rolenonplayinggame.domain.model.Character

/**
 * Composable that displays a Tiled map using libGDX rendering.
 * This integrates libGDX with Jetpack Compose using AndroidView.
 */
@Composable
fun TiledMapView(
    mapPath: String = "maps/aethermoor.tmx",
    characters: List<Character> = emptyList(),
    onMapReady: (TiledMapRenderer) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                // Create the libGDX application
                val gdxApp = object : AndroidApplication() {
                    override fun createLayoutParams(): View.LayoutParams {
                        val params = super.createLayoutParams()
                        params.width = View.LayoutParams.MATCH_PARENT
                        params.height = View.LayoutParams.MATCH_PARENT
                        return params
                    }
                }

                // Create the map renderer
                val mapRenderer = TiledMapRenderer(ctx)

                // Create the application listener
                val appListener = createMapApplicationListener(
                    mapRenderer = mapRenderer,
                    mapPath = mapPath,
                    onMapReady = { onMapReady(mapRenderer) }
                )

                // Configure libGDX
                val config = AndroidApplicationConfiguration().apply {
                    useAccelerometer = false
                    useCompass = false
                    useGyroscope = false
                }

                // Initialize libGDX with the application listener
                gdxApp.initialize(appListener, config)

                gdxApp
            },
            update = { gdxView ->
                // Update characters when the list changes
                // Note: This requires accessing the renderer which is tricky
                // For now, we'll handle updates through the ViewModel
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            // Cleanup is handled by libGDX's lifecycle
        }
    }
}

/**
 * Creates an ApplicationListener for rendering the Tiled map
 */
private fun createMapApplicationListener(
    mapRenderer: TiledMapRenderer,
    mapPath: String,
    onMapReady: () -> Unit
): ApplicationListener {
    return object : ApplicationListener {
        override fun create() {
            mapRenderer.initialize(
                viewportWidth = Gdx.graphics.width.toFloat(),
                viewportHeight = Gdx.graphics.height.toFloat()
            )
            mapRenderer.loadMap(mapPath)

            if (mapRenderer.isMapLoaded()) {
                onMapReady()
            }
        }

        override fun render() {
            mapRenderer.render()
        }

        override fun resize(width: Int, height: Int) {
            mapRenderer.resize(width, height)
        }

        override fun pause() {
            // Handle app pause if needed
        }

        override fun resume() {
            // Handle app resume if needed
        }

        override fun dispose() {
            mapRenderer.dispose()
        }
    }
}
