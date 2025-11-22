# Isometric Rendering Research for Android

Research conducted: 2025-11-22

## Executive Summary

This document presents research findings on isometric tile-based rendering solutions for Android games built with Kotlin. The goal is to find suitable libraries/frameworks for:

1. Isometric tile-based map rendering
2. Character sprites aligned with the isometric world
3. Proper map display functionality
4. Future capability to show heroes in rooms

## Key Findings

### Main Challenge
There are **no dedicated Kotlin-native libraries** specifically designed for isometric rendering on Android. However, several established game frameworks support isometric tilemaps through the **Tiled Map Editor** (TMX format) integration.

### Recommended Approach
**libGDX + Tiled Map Editor** is the most mature and well-documented solution for Kotlin/Android isometric games.

---

## Game Frameworks & Engines

### 1. libGDX ⭐ (RECOMMENDED)

**Status:** Active, mature, well-documented

**Pros:**
- Native isometric support via `IsometricTiledMapRenderer`
- Excellent integration with Tiled Map Editor (TMX format)
- Cross-platform (Android, iOS, Desktop, Web)
- Large community and extensive tutorials
- Java-based but fully compatible with Kotlin
- Built-in sprite batching and rendering optimizations

**Cons:**
- The isometric renderer is marked as "experimental" in documentation
- Rendering isometric maps on mobile is costly (every tile requires blending)
- Larger learning curve than simple 2D libraries

**Key Classes:**
```kotlin
// For isometric maps
IsometricTiledMapRenderer

// For orthogonal maps (comparison)
OrthogonalTiledMapRenderer
```

**Documentation & Resources:**
- [Tile maps - libGDX](https://libgdx.com/wiki/graphics/2d/tile-maps)
- [IsometricTiledMapRenderer source](https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/maps/tiled/renderers/IsometricTiledMapRenderer.java)
- [GitHub Example: IsometricTest](https://github.com/unenergizer/IsometricTest)
- Stack Overflow discussions on [2d isometric maps in libGDX](https://stackoverflow.com/questions/48856467/2d-isometric-maps-in-libgdx)

**Integration Steps:**
1. Add libGDX to your Gradle dependencies
2. Create isometric maps in Tiled Map Editor
3. Export as TMX format
4. Load maps using `TmxMapLoader`
5. Render using `IsometricTiledMapRenderer`

---

### 2. AndEngine

**Status:** ⚠️ **DEPRECATED** (last updated 2+ years ago)

**Pros:**
- Can load TMX format (Tiled Map Editor)
- Supports isometric maps
- Historically popular for Android games

**Cons:**
- **No longer actively maintained**
- No recent tutorials (most from 2011-2013)
- Better alternatives exist

**Verdict:** Not recommended for new projects in 2024-2025.

**References:**
- [Isometric Game Engine for Android - Stack Overflow](https://stackoverflow.com/questions/5849782/isometric-game-engine-for-android)
- [How to Create a Simple Android Game with AndEngine - Kodeco](https://www.kodeco.com/2916-how-to-create-a-simple-android-game-with-andengine)

---

### 3. Cocos2d-x

**Status:** Active, but primarily C++ focused

**Pros:**
- Full isometric tilemap support via `TMXTiledMap`
- Supports Tiled Map Editor (orthogonal, isometric, hexagonal)
- Cross-platform engine
- Mature and stable

**Cons:**
- **Primarily a C++ framework** (not Kotlin-native)
- Using with Kotlin/Android requires JNI bridging
- More complex setup than libGDX for Android/Kotlin
- Learning curve for developers unfamiliar with C++

**Verdict:** Viable but not ideal for Kotlin-first Android development.

**References:**
- [Isometric Tile maps in Cocos2d-x](https://gamedev.stackexchange.com/questions/31945/isometric-tile-maps-in-cocos2d-x)
- [TileMap Documentation](https://docs.cocos2d-x.org/cocos2d-x/v3/en/other_node_types/tilemap.html)
- [Cocos2D-X Tile Map Tutorial - Kodeco](https://www.kodeco.com/2684-cocos2d-x-tile-map-tutorial-part-1)
- [GitHub: IsoTilemap Sample](https://github.com/bennyk/IsoTilemap)

---

### 4. Jetpack Compose (Custom Implementation)

**Status:** Modern, Android-native, but requires manual implementation

**Pros:**
- Native Kotlin/Android solution
- Leverages Skia graphics engine for high-performance 2D rendering
- Full `Canvas` API for custom drawing
- Modern declarative UI paradigm
- No external game engine dependencies

**Cons:**
- **No built-in isometric support** - must implement coordinate transformation manually
- No TMX/Tiled integration out of the box
- Performance may be lower than specialized game engines
- Only supports 2D graphics
- Would need to build entire rendering pipeline yourself

**Use Case:** Only if you want a pure Kotlin/Compose solution and are willing to implement isometric math yourself.

**References:**
- [Unleashing the Power of Android Jetpack Compose](https://30dayscoding.com/blog/android-jetpack-compose-game-development)
- [Mastering Android Jetpack Compose - Canvas Rendering](https://30dayscoding.com/blog/android-jetpack-compose-graphics-canvas-rendering)
- [GitHub: compose-game (simple 2D engine)](https://github.com/vgupta98/compose-game)
- [Graphics in Compose - Android Developers](https://developer.android.com/develop/ui/compose/graphics/draw/overview)

---

### 5. MapView (Kotlin-native, but not for games)

**Status:** Active, Kotlin-focused

**Type:** Map display library (for geographic/cartographic use, not game maps)

**Pros:**
- Idiomatic Kotlin (coroutines, flows)
- Fast, memory-efficient
- Designed for Android

**Cons:**
- **Not designed for game development**
- Meant for tiled geographic maps (like Google Maps), not isometric game worlds
- No sprite/character support

**Verdict:** Not suitable for this use case.

**References:**
- [GitHub: MapView](https://github.com/p-lr/MapView)

---

## Tiled Map Editor ⭐

**Essential Tool:** Free, open-source map editor

**Why It's Important:**
- Industry standard for 2D/isometric tile-based games
- Supports orthogonal, isometric, and hexagonal maps
- Exports to TMX/JSON formats
- Integrated with most game engines (libGDX, Cocos2d-x, Unity, Godot, etc.)

**Features:**
- Visual tile placement
- Multiple layers (ground, objects, decorations)
- Object placement with custom properties
- Collision layer support
- Tileset management

**Website:** [mapeditor.org](http://www.mapeditor.org/)

**Supported Frameworks:**
- [Libraries and Frameworks for TMX maps](https://doc.mapeditor.org/en/stable/reference/support-for-tmx-maps/)

**Tutorial:**
- [Creating An Isometric Map Using Tiled Tutorial - GameFromScratch](https://gamefromscratch.com/creating-an-isometric-map-using-tiled-tutorial/)

---

## Technical Challenges: Sprite Alignment & Depth Sorting

### The Core Problem

Isometric games face a fundamental rendering challenge: **determining the correct draw order** for sprites and tiles so objects appear correctly in 3D space.

### Challenge Details

1. **Sprite Origin Points**
   - Isometric tile origin: typically the **top corner** of the tile
   - Sprite origin: typically **top-left corner** (must be adjusted)
   - **Solution:** For characters, place origin point **between the feet**, not at head or top-left

2. **Rendering Order**
   - Objects closer to the camera (lower Y) should be drawn later (on top)
   - Multi-tile objects complicate this significantly
   - Sprites and tiles are different concepts and must be sorted together

### Solutions

#### 1. Z-Order Sorting (Simple)
- Assign each object a Y-based depth value
- Sort all objects by Y coordinate before rendering
- Render from back to front

#### 2. 3D Coordinate System (Recommended)
- Position objects in 3D with x, y, and z coordinates
- Sort using the z position at render time
- Requires proper origin point setup

#### 3. Topological Sorting (Advanced)
- Build dependency graph using axis-aligned bounding boxes (AABB)
- Compare 3D bounds of sprites
- More complex but handles edge cases better

#### 4. Using Tiled's Z Properties
- Assign `z` properties to objects in Tiled
- Use depth sorting utilities (like `tiled-utils` for JavaScript)
- Sort sprites using `byDepth` methods

**References:**
- [Isometric depth sorting - Tiled Forum](https://discourse.mapeditor.org/t/isometric-depth-sorting/736)
- [How do I determine the draw order of isometric 2D objects - Game Dev SE](https://gamedev.stackexchange.com/questions/103442/how-do-i-determine-the-draw-order-of-isometric-2d-objects-occupying-multiple-til)
- [How do I use tiles and sprites together in an isometric scene?](https://gamedev.stackexchange.com/questions/74477/how-do-i-use-tiles-and-sprites-together-in-an-isometric-scene)

---

## Isometric Math Fundamentals

### Coordinate Conversion

Isometric games use simple math to convert game coordinates to screen coordinates:

**Key Formula:**
- Increasing map X by +1 tile → increases screen.x by **half tile width**, screen.y by **half tile height**
- Increasing map Y by +1 tile → decreases screen.x by **half tile width**, screen.y by **half tile height**

**Typical Conversion (pseudo-code):**
```kotlin
// Game coordinates to screen coordinates
screenX = (gameX - gameY) * (tileWidth / 2)
screenY = (gameX + gameY) * (tileHeight / 2)

// Screen coordinates to game coordinates
gameX = (screenX / (tileWidth / 2) + screenY / (tileHeight / 2)) / 2
gameY = (screenY / (tileHeight / 2) - screenX / (tileWidth / 2)) / 2
```

**References:**
- [Isometric Tiles Math - Clint Bellanger](https://clintbellanger.net/articles/isometric_math/)
- [Drawing Isometric game worlds - Stack Overflow](https://stackoverflow.com/questions/892811/drawing-isometric-game-worlds)

---

## Free Isometric Asset Resources

To test your implementation or build prototypes:

### itch.io (Highly Recommended)
- **443 Isometric Town and Roof Tiles** (free)
- **1,008 Isometric Floor Tiles** (free)
- **395 Isometric Object Tiles** (free)
- **1,872 Isometric Wall Tiles** (free)
- Thousands of character sprites for RPG/dungeon crawlers

**Links:**
- [Top free game assets tagged Isometric](https://itch.io/game-assets/free/tag-isometric)
- [Isometric + Tileset](https://itch.io/game-assets/tag-isometric/tag-tileset)
- [Isometric + Sprites](https://itch.io/game-assets/tag-isometric/tag-sprites)

### OpenGameArt.org
- Isometric tiles
- Isometric characters
- All freely licensed

**Links:**
- [Isometric Tiles - OpenGameArt](https://opengameart.org/content/isometric-tiles)

### Commercial Options
- **GraphicRiver**: Professional isometric game assets, tilesets, sprite sheets
- **RetroStyleGames**: Isometric sprites for 2D/3D games (characters, environments, tiles)

**References:**
- [Isometric Sprites - RetroStyleGames](https://retrostylegames.com/outsourcing/3d-2d-game-sprites/)
- [Isometric Game Assets - GraphicRiver](https://graphicriver.net/graphics-with-isometric-in-game-assets)

---

## Recommendations for Role Non-Playing Game

### ⭐ Primary Recommendation: libGDX + Tiled Map Editor

**Rationale:**
1. **Mature ecosystem** - battle-tested in many shipped games
2. **Full isometric support** - `IsometricTiledMapRenderer` handles the heavy lifting
3. **Tiled integration** - visual map editor makes iteration fast
4. **Kotlin compatible** - works seamlessly with your existing Kotlin codebase
5. **Active community** - abundant tutorials, Stack Overflow answers, examples
6. **Cross-platform** - could expand to desktop/web later if desired

**Implementation Path:**
1. **Week 1: Setup & Learning**
   - Add libGDX to project dependencies
   - Study basic libGDX rendering examples
   - Create simple test isometric map in Tiled

2. **Week 2: Isometric Rendering**
   - Implement `IsometricTiledMapRenderer`
   - Load and display test map
   - Implement camera controls (pan/zoom)

3. **Week 3: Character Integration**
   - Add character sprites
   - Implement depth sorting for characters
   - Test character movement aligned to tiles

4. **Week 4: Room System (Future)**
   - Design room layout templates in Tiled
   - Implement room loading/switching
   - Add character-to-room assignment

**Example Code Structure:**
```kotlin
class GameScreen : ScreenAdapter() {
    private lateinit var camera: OrthographicCamera
    private lateinit var tiledMap: TiledMap
    private lateinit var renderer: IsometricTiledMapRenderer

    override fun show() {
        camera = OrthographicCamera()
        tiledMap = TmxMapLoader().load("maps/world.tmx")
        renderer = IsometricTiledMapRenderer(tiledMap)
    }

    override fun render(delta: Float) {
        camera.update()
        renderer.setView(camera)
        renderer.render()

        // Render characters with depth sorting
        renderCharacters()
    }

    private fun renderCharacters() {
        // Sort characters by Y position (depth)
        // Render from back to front
    }
}
```

### Alternative: Jetpack Compose (Only if avoiding game engines)

**Use this if:**
- You want a pure Kotlin/Compose solution
- You're comfortable implementing isometric math yourself
- Your game is simple enough not to need a full game engine
- You want to leverage existing Compose UI components

**Trade-offs:**
- More initial development time
- No Tiled Map Editor support (would need custom map format)
- Lower performance than specialized engines
- More code to maintain

---

## Implementation Checklist

### Phase 1: Foundation
- [ ] Add libGDX dependencies to `build.gradle.kts`
- [ ] Download and install Tiled Map Editor
- [ ] Create simple test isometric tileset (or use free assets)
- [ ] Create 10x10 test map in Tiled
- [ ] Load and render map in Android app

### Phase 2: Camera & Controls
- [ ] Implement camera zoom
- [ ] Implement camera pan
- [ ] Add touch controls for mobile
- [ ] Test performance on target Android devices

### Phase 3: Sprite Integration
- [ ] Load character sprite sheets
- [ ] Implement sprite animation
- [ ] Add sprites to isometric world
- [ ] Implement depth sorting algorithm
- [ ] Test multi-character rendering

### Phase 4: World System
- [ ] Design Aethermoor regions in Tiled (5 regions from WORLD_LORE.md)
- [ ] Implement region loading/unloading
- [ ] Add fog-of-war system
- [ ] Test map transitions

### Phase 5: Character Rooms (Future)
- [ ] Design room templates in Tiled
- [ ] Implement room instantiation
- [ ] Add character-to-room assignment
- [ ] Implement room switching

---

## Performance Considerations

### Mobile Optimization Tips

1. **Texture Atlasing**
   - Combine tilesets into texture atlases
   - Reduces draw calls significantly
   - libGDX handles this automatically with proper setup

2. **Frustum Culling**
   - Only render tiles visible on screen
   - libGDX's camera provides frustum automatically

3. **Sprite Batching**
   - Batch character sprites with tiles where possible
   - Minimize state changes (texture swaps)

4. **Layer Management**
   - Only render layers that are visible
   - Disable off-screen layers

5. **Texture Compression**
   - Use ETC2 compression for Android
   - Reduces memory footprint

### Expected Performance
- **Target:** 60 FPS on mid-range Android devices (SDK 31+)
- **Map Size:** Up to 100x100 tiles (with culling)
- **Characters:** 20-50 simultaneous characters on screen

---

## Additional Resources

### General Isometric Game Development
- [Isometric Game Development Tools and Engines: A 2025 Guide](https://vocal.media/gamers/isometric-game-development-tools-and-engines-a-2025-guide-bp3kg09sw)
- [Isometric 2D environments with Tilemap - Unity Blog](https://unity.com/blog/engine-platform/isometric-2d-environments-with-tilemap) (Unity-focused but concepts apply)
- [An Interesting Journey in Creating a 2D Isometric Platformer](https://ernestine-games.fr/en/an-interesting-journey-in-creating-a-2d-isometric-platformer-by-sven-duval/)

### libGDX Specific
- [Official libGDX Wiki - Tile Maps](https://libgdx.com/wiki/graphics/2d/tile-maps)
- [Tiled Integration Documentation](https://doc.mapeditor.org/en/stable/reference/support-for-tmx-maps/)

### Community & Support
- [libGDX Discord](https://discord.gg/6pgDK9F)
- [r/libgdx subreddit](https://www.reddit.com/r/libgdx/)
- [Tiled Forum](https://discourse.mapeditor.org/)

---

## Conclusion

For **Role Non-Playing Game**, the recommended approach is:

**libGDX + Tiled Map Editor + Isometric Rendering**

This combination provides:
✅ Mature, proven technology
✅ Full isometric support out of the box
✅ Visual map editing tools
✅ Excellent Kotlin compatibility
✅ Active community support
✅ Good mobile performance
✅ Path to cross-platform if needed

The main trade-off is the learning curve for libGDX, but the time investment pays off with faster iteration and fewer custom systems to maintain.

---

## Next Steps

1. **Prototype Decision:** Create a minimal proof-of-concept with libGDX
   - 1-2 days to set up basic isometric rendering
   - Test on target Android device
   - Validate performance and developer experience

2. **If Successful:** Proceed with libGDX integration into main project
3. **If Issues Arise:** Re-evaluate Jetpack Compose custom implementation

---

**Research Date:** 2025-11-22
**Researcher:** Claude Code
**Project:** Role Non-Playing Game
**Target Platform:** Android (SDK 31+, Kotlin)
