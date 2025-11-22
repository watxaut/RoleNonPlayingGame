package com.watxaut.rolenonplayinggame.presentation.map

import android.content.Context
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector3
import com.watxaut.rolenonplayinggame.domain.model.Character

/**
 * Handles rendering of Tiled TMX maps using libGDX.
 * Uses OrthogonalTiledMapRenderer (standard top-down view) for stable rendering.
 */
class TiledMapRenderer(
    private val context: Context
) {
    private var tiledMap: TiledMap? = null
    private var mapRenderer: OrthogonalTiledMapRenderer? = null
    private var spriteBatch: SpriteBatch? = null

    val camera: OrthographicCamera = OrthographicCamera()

    // Camera controls
    var cameraX: Float = 0f
    var cameraY: Float = 0f
    var zoom: Float = 1f
        set(value) {
            field = value.coerceIn(MIN_ZOOM, MAX_ZOOM)
            camera.zoom = field
            camera.update()
        }

    // Map properties
    var mapWidth: Int = 0
        private set
    var mapHeight: Int = 0
        private set
    var tileWidth: Int = 0
        private set
    var tileHeight: Int = 0
        private set

    private var characters: List<Character> = emptyList()

    companion object {
        const val MIN_ZOOM = 0.3f
        const val MAX_ZOOM = 3f
        private const val DEFAULT_MAP_FILE = "maps/aethermoor.tmx"
    }

    /**
     * Initialize the map renderer with viewport size
     */
    fun initialize(viewportWidth: Float, viewportHeight: Float) {
        camera.setToOrtho(false, viewportWidth, viewportHeight)
        camera.update()

        spriteBatch = SpriteBatch()
    }

    /**
     * Load a Tiled map from assets
     * @param mapPath Path to the TMX file in assets (e.g., "maps/aethermoor.tmx")
     */
    fun loadMap(mapPath: String = DEFAULT_MAP_FILE) {
        try {
            // Dispose existing map if any
            disposeMap()

            // Load the map using TmxMapLoader
            tiledMap = TmxMapLoader().load(mapPath)

            // Extract map properties
            tiledMap?.let { map ->
                val properties = map.properties
                mapWidth = properties.get("width", Int::class.java) ?: 0
                mapHeight = properties.get("height", Int::class.java) ?: 0
                tileWidth = properties.get("tilewidth", Int::class.java) ?: 32
                tileHeight = properties.get("tileheight", Int::class.java) ?: 32

                // Create renderer for orthogonal maps (standard top-down view)
                mapRenderer = OrthogonalTiledMapRenderer(map)

                // Center camera on map
                centerCameraOnMap()
            }
        } catch (e: Exception) {
            Gdx.app?.error("TiledMapRenderer", "Failed to load map: $mapPath", e)
            // Map loading failed - this is expected if the TMX file doesn't exist yet
            // The user will need to create it in Tiled Map Editor
        }
    }

    /**
     * Center the camera on the map
     */
    private fun centerCameraOnMap() {
        cameraX = (mapWidth * tileWidth) / 2f
        cameraY = (mapHeight * tileHeight) / 2f
        updateCameraPosition()
    }

    /**
     * Update camera position
     */
    fun updateCameraPosition() {
        camera.position.set(cameraX, cameraY, 0f)
        camera.update()
    }

    /**
     * Pan the camera by delta pixels
     */
    fun panCamera(deltaX: Float, deltaY: Float) {
        cameraX += deltaX / zoom
        cameraY += deltaY / zoom

        // Clamp camera to map bounds
        val maxX = mapWidth * tileWidth.toFloat()
        val maxY = mapHeight * tileHeight.toFloat()
        val viewportHalfWidth = camera.viewportWidth * zoom / 2
        val viewportHalfHeight = camera.viewportHeight * zoom / 2

        cameraX = cameraX.coerceIn(viewportHalfWidth, maxX - viewportHalfWidth)
        cameraY = cameraY.coerceIn(viewportHalfHeight, maxY - viewportHalfHeight)

        updateCameraPosition()
    }

    /**
     * Update the list of characters to render on the map
     */
    fun updateCharacters(newCharacters: List<Character>) {
        characters = newCharacters
    }

    /**
     * Render the map and characters
     */
    fun render() {
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        mapRenderer?.let { renderer ->
            renderer.setView(camera)
            renderer.render()
        }

        // Render characters on top of the map
        renderCharacters()
    }

    /**
     * Render character markers on the map
     */
    private fun renderCharacters() {
        spriteBatch?.let { batch ->
            batch.projectionMatrix = camera.combined
            batch.begin()

            // TODO: Render character sprites
            // For now, this is a placeholder
            // You'll need to load character sprites and render them at their locations

            batch.end()
        }
    }

    /**
     * Convert screen coordinates to world coordinates
     */
    fun screenToWorld(screenX: Float, screenY: Float): Vector3 {
        val worldCoords = Vector3(screenX, screenY, 0f)
        camera.unproject(worldCoords)
        return worldCoords
    }

    /**
     * Convert world coordinates to tile coordinates
     */
    fun worldToTile(worldX: Float, worldY: Float): Pair<Int, Int> {
        val tileX = (worldX / tileWidth).toInt()
        val tileY = (worldY / tileHeight).toInt()
        return tileX to tileY
    }

    /**
     * Resize the viewport
     */
    fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
    }

    /**
     * Dispose of resources
     */
    fun dispose() {
        disposeMap()
        spriteBatch?.dispose()
        spriteBatch = null
    }

    private fun disposeMap() {
        mapRenderer?.dispose()
        mapRenderer = null
        tiledMap?.dispose()
        tiledMap = null
    }

    /**
     * Check if a map is currently loaded
     */
    fun isMapLoaded(): Boolean = tiledMap != null
}
