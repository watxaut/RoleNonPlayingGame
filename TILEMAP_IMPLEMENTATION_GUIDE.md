# Tiled Map Implementation Guide

**Date:** 2025-11-22
**Status:** Ready for Implementation
**Map Type:** Orthogonal (Top-Down) - Stable and Production-Ready

---

## Overview

This guide explains how to implement the Aethermoor world map using **libGDX + Tiled Map Editor** with **orthogonal (top-down) rendering**. We're using orthogonal instead of isometric because libGDX's isometric renderer is experimental.

## What's Been Done ✅

I've set up the foundation for you:

### 1. ✅ Dependencies Added
- Added libGDX 1.12.1 to `gradle/libs.versions.toml`
- Added all required libGDX libraries to `app/build.gradle.kts`
- Includes native libraries for all Android architectures (ARM, x86)

### 2. ✅ Code Infrastructure Created
Two new files in `app/src/main/java/com/watxaut/rolenonplayinggame/presentation/map/`:

**TiledMapRenderer.kt**
- Handles loading TMX maps from assets
- Manages camera (pan, zoom, bounds checking)
- Renders maps using `OrthogonalTiledMapRenderer`
- Supports character rendering on top of maps
- Provides screen-to-world and world-to-tile coordinate conversion

**TiledMapView.kt**
- Jetpack Compose integration for libGDX
- Uses `AndroidView` to embed libGDX rendering
- Handles libGDX lifecycle (create, render, resize, dispose)
- Ready to integrate with your existing Compose UI

### 3. ✅ Map Specifications Designed
See "Aethermoor Map Specification" below for complete details.

---

## What You Need to Do 🔧

### Phase 1: Install Tools & Get Assets

#### Step 1.1: Install Tiled Map Editor
1. Download from: **https://www.mapeditor.org/**
2. Install the latest stable version (currently 1.11.0)
3. Launch Tiled and familiarize yourself with the interface

#### Step 1.2: Get Tileset Graphics
You have two options:

**Option A: Use Free Assets (Recommended for Prototyping)**
1. Visit https://itch.io/game-assets/free/tag-tileset
2. Download a suitable fantasy/RPG tileset
3. Recommended: Search for "32x32 fantasy tileset" or "top-down RPG tiles"
4. Good free options:
   - "Dungeon Tileset" by 0x72
   - "Overworld Tileset" by ArMM1998
   - Any 16x16 or 32x32 fantasy ground tiles

**Option B: Create Your Own Tileset**
1. Create PNG images with tiles in a grid
2. Recommended tile size: 32x32 pixels
3. Include: grass, water, sand, snow, stone, path tiles
4. Save in `app/src/main/assets/tilesets/` directory

**What You Need (Minimum):**
- **Ground tiles**: grass, dirt, water, sand, snow, stone
- **Path tiles**: cobblestone or dirt paths
- **Decoration tiles** (optional): trees, rocks, flowers
- **Structure tiles** (optional): buildings, walls

---

### Phase 2: Create the Map in Tiled

#### Step 2.1: Create Project Structure
```
app/src/main/assets/
├── maps/
│   └── aethermoor.tmx          (created in Tiled)
└── tilesets/
    ├── ground_tiles.png         (your tileset image)
    └── ground_tiles.tsx         (optional: Tiled tileset definition)
```

#### Step 2.2: Create the Map in Tiled

1. **Launch Tiled** → File → New → New Map

2. **Map Settings:**
   - **Orientation:** Orthogonal ✅
   - **Tile layer format:** CSV or Base64 (zlib compressed)
   - **Tile render order:** Right Down
   - **Map size:**
     - Width: 100 tiles
     - Height: 100 tiles
   - **Tile size:**
     - Width: 32 pixels
     - Height: 32 pixels

3. **Click "Save As"** and name it `aethermoor.tmx`
   - Save location: `app/src/main/assets/maps/aethermoor.tmx`

#### Step 2.3: Add Your Tileset to the Map

1. In Tiled, go to: **Map → New Tileset**
2. **Tileset Settings:**
   - Name: "GroundTiles" (or whatever you want)
   - Type: Based on Tileset Image
   - Source: Browse to your tileset PNG file
   - Tile width: 32px
   - Tile height: 32px
   - Margin: 0
   - Spacing: 0 (or adjust based on your tileset)
3. Click **Save As External Tileset** (recommended)
   - Save as: `app/src/main/assets/tilesets/ground_tiles.tsx`

#### Step 2.4: Create Map Layers

Create these layers (in order from bottom to top):

1. **Layer: "base_terrain"** (Tile Layer)
   - Fill the entire map with a base terrain (grass for Heartlands)
   - Use the bucket fill tool for speed

2. **Layer: "regions"** (Tile Layer)
   - Paint distinct terrain for each region:
     - **Heartlands** (center): Grass, flowers
     - **Thornwood Wilds**: Dark grass, forest tiles
     - **Ashenveil Desert**: Sand, dry earth
     - **Frostpeak Mountains**: Snow, ice
     - **Stormcoast Reaches**: Rocky, coastal

3. **Layer: "paths"** (Tile Layer)
   - Add paths connecting locations
   - Use cobblestone or dirt path tiles

4. **Layer: "decorations"** (Tile Layer)
   - Trees, rocks, flowers, grass tufts
   - Make regions visually distinct

5. **Layer: "locations"** (Object Layer) ⭐ IMPORTANT
   - Add point objects for each location
   - Use **Insert Point** tool (or Insert → Point)
   - For each location, set properties:
     - **Name:** Location name (e.g., "Havenmoor")
     - **Type:** Location type (e.g., "town", "dungeon", "field")
     - **Custom Properties:**
       - `locationId` (string): e.g., "heartlands_havenmoor"
       - `region` (string): e.g., "HEARTLANDS"
       - `levelMin` (int): Minimum character level
       - `levelMax` (int): Maximum character level

#### Step 2.5: Add Map Properties

1. Click on the map background (deselect all)
2. In Properties panel, add custom properties:
   - **worldName** (string): "Aethermoor"
   - **worldVersion** (string): "1.0"
   - **maxPlayers** (int): 100 (or whatever you want)

#### Step 2.6: Save and Export

1. **File → Save** (`aethermoor.tmx`)
2. Verify the file is in: `app/src/main/assets/maps/aethermoor.tmx`

---

### Phase 3: Aethermoor Map Specification

Follow this layout when painting your map:

```
Map Layout (100x100 tiles):
┌─────────────────────────────────────────────────┐
│                                                 │
│     [Frostpeak Mountains]    [Stormcoast]      │
│          (Snow)                 (Rocky)         │
│                                                 │
│                                                 │
│                                                 │
│  [Thornwood]          [Heartlands]              │
│   (Forest)           (Grass/Flowers)            │
│                         ★ START                 │
│                                                 │
│                    [Ashenveil Desert]           │
│                         (Sand)                  │
│                                                 │
└─────────────────────────────────────────────────┘
```

### Region Coordinates Guide

Use these approximate positions when painting regions:

| Region | Tile Position (X, Y) | Tile Size | Terrain Colors |
|--------|---------------------|-----------|----------------|
| **Heartlands** | (40-60, 40-60) | ~20x20 | Green, light green, yellow flowers |
| **Thornwood Wilds** | (20-40, 50-70) | ~20x20 | Dark green, brown, deep forest |
| **Ashenveil Desert** | (45-70, 20-40) | ~25x20 | Sand yellow, tan, orange |
| **Frostpeak Mountains** | (20-45, 70-90) | ~25x20 | White, light blue, ice blue |
| **Stormcoast Reaches** | (60-85, 70-90) | ~25x20 | Dark gray, blue-gray, stormy blue |

### Location Points to Add

Add these locations as **Point Objects** in the "locations" layer:

#### Heartlands (Levels 1-10)
1. **Havenmoor** - Starting town
   - Position: (50, 50)
   - Properties: `locationId="heartlands_havenmoor"`, `type="town"`, `levelMin=1`, `levelMax=5`

2. **Meadowbrook Fields**
   - Position: (52, 48)
   - Properties: `locationId="heartlands_meadowbrook_fields"`, `type="field"`, `levelMin=1`, `levelMax=3`

3. **Whispering Woods**
   - Position: (48, 52)
   - Properties: `locationId="heartlands_whispering_woods"`, `type="forest"`, `levelMin=3`, `levelMax=7`

4. **Old Mill**
   - Position: (54, 52)
   - Properties: `locationId="heartlands_old_mill"`, `type="landmark"`, `levelMin=5`, `levelMax=10`

#### Thornwood Wilds (Levels 11-20)
5. **Thornvale Village**
   - Position: (30, 60)
   - Properties: `locationId="thornwood_thornvale"`, `type="village"`, `levelMin=11`, `levelMax=15`

6. **Ancient Ruins**
   - Position: (28, 64)
   - Properties: `locationId="thornwood_ancient_ruins"`, `type="ruins"`, `levelMin=13`, `levelMax=18`

#### Ashenveil Desert (Levels 21-30)
7. **Oasis of Mir**
   - Position: (58, 30)
   - Properties: `locationId="ashenveil_oasis_mir"`, `type="oasis"`, `levelMin=21`, `levelMax=25`

8. **Sandstone Catacombs**
   - Position: (62, 28)
   - Properties: `locationId="ashenveil_catacombs"`, `type="dungeon"`, `levelMin=25`, `levelMax=30`

#### Frostpeak Mountains (Levels 31-40)
9. **Frostholm**
   - Position: (32, 80)
   - Properties: `locationId="frostpeak_frostholm"`, `type="town"`, `levelMin=31`, `levelMax=35`

10. **Crystal Caverns**
    - Position: (35, 83)
    - Properties: `locationId="frostpeak_crystal_caverns"`, `type="cave"`, `levelMin=35`, `levelMax=40`

#### Stormcoast Reaches (Levels 41-50)
11. **Stormwatch Harbor**
    - Position: (72, 80)
    - Properties: `locationId="stormcoast_stormwatch"`, `type="port"`, `levelMin=41`, `levelMax=45`

12. **Kraken's Lair**
    - Position: (75, 78)
    - Properties: `locationId="stormcoast_krakens_lair"`, `type="boss_lair"`, `levelMin=48`, `levelMax=50`

---

### Phase 4: Integrate with Your App

#### Step 4.1: Create Assets Directory

In Android Studio:
1. Right-click on `app/src/main/`
2. New → Directory → Name it `assets`
3. Create subdirectories:
   - `assets/maps/`
   - `assets/tilesets/`

4. Copy your files:
   - `aethermoor.tmx` → `assets/maps/`
   - `ground_tiles.png` → `assets/tilesets/`
   - `ground_tiles.tsx` → `assets/tilesets/` (if you created it)

#### Step 4.2: Update WorldMapScreen (Option 1: Replace Current Implementation)

You can replace the current custom isometric drawing with the new Tiled map:

```kotlin
// In WorldMapScreen.kt
@Composable
fun WorldMapScreen(
    modifier: Modifier = Modifier,
    viewModel: WorldMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mapRenderer by remember { mutableStateOf<TiledMapRenderer?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        // Use the new TiledMapView
        TiledMapView(
            mapPath = "maps/aethermoor.tmx",
            characters = uiState.characters,
            onMapReady = { renderer ->
                mapRenderer = renderer
            },
            modifier = Modifier.fillMaxSize()
        )

        // Keep your existing UI overlays:
        // - Title card at top
        // - Legend at bottom left
        // - Zoom controls at bottom right

        // ... rest of your existing UI code
    }
}
```

**OR**

#### Step 4.2: Alternative - Add New Tab (Option 2: Keep Both)

Add a new tab/screen for the Tiled map view while keeping the existing custom map:

1. Create `TiledWorldMapScreen.kt` in the same package
2. Add it to your navigation
3. Users can switch between views

#### Step 4.3: Sync Build

1. In Android Studio: **File → Sync Project with Gradle Files**
2. Wait for sync to complete
3. Build should succeed with new libGDX dependencies

---

## Testing Your Map

### Step 1: Build and Run
```bash
./gradlew assembleDebug
```

Install on device/emulator and navigate to the World Map screen.

### Step 2: Verify Map Loading

If the map doesn't load:
1. Check Logcat for errors: Filter by "TiledMapRenderer"
2. Verify file path: `maps/aethermoor.tmx` exists in assets
3. Verify tileset path in TMX file is relative: `tilesets/ground_tiles.png`

### Step 3: Test Controls

- **Pan:** Drag with one finger
- **Zoom:** Pinch gesture
- **Tap locations:** Should show location details (if you add tap handling)

---

## Map Camera Controls Reference

The `TiledMapRenderer` provides these controls:

```kotlin
// In your ViewModel or Screen:
mapRenderer.panCamera(deltaX = 100f, deltaY = 50f)
mapRenderer.zoom = 1.5f  // 0.3f to 3f range
mapRenderer.updateCameraPosition()

// Convert screen tap to tile:
val worldCoords = mapRenderer.screenToWorld(screenX, screenY)
val (tileX, tileY) = mapRenderer.worldToTile(worldCoords.x, worldCoords.y)

// Check what location is at that tile (you'll implement this)
```

---

## Adding Touch Interaction

To make locations tappable, you'll need to:

1. **Capture touch events** in TiledMapView
2. **Convert screen coords** to world coords
3. **Check object layer** for locations at that position
4. **Show location dialog** like your current implementation

Example code structure:

```kotlin
// In TiledMapRenderer.kt, add:
fun getLocationAt(worldX: Float, worldY: Float): MapLocation? {
    // Query the "locations" object layer from the TiledMap
    // Check if any location object contains this point
    // Return the location if found
    return null // TODO: Implement
}

// Then in your Screen/ViewModel:
// On tap:
val worldCoords = mapRenderer.screenToWorld(tapX, tapY)
val location = mapRenderer.getLocationAt(worldCoords.x, worldCoords.y)
location?.let {
    // Show location dialog
}
```

---

## Advanced: Adding Character Sprites

### Step 1: Get Character Sprites
- Download or create 32x32 character sprites
- Save to `assets/sprites/characters/`

### Step 2: Load Sprites in TiledMapRenderer

```kotlin
// Add to TiledMapRenderer.kt:
private val characterTextures = mutableMapOf<String, Texture>()

fun loadCharacterSprite(jobClass: String, spritePath: String) {
    characterTextures[jobClass] = Texture(Gdx.files.internal(spritePath))
}

// Update renderCharacters():
private fun renderCharacters() {
    spriteBatch?.let { batch ->
        batch.projectionMatrix = camera.combined
        batch.begin()

        characters.forEach { character ->
            val texture = characterTextures[character.jobClass.name]
            if (texture != null) {
                // Convert character location to world coordinates
                val (tileX, tileY) = getLocationTileCoords(character.currentLocation)
                val worldX = tileX * tileWidth.toFloat()
                val worldY = tileY * tileHeight.toFloat()

                // Draw sprite
                batch.draw(texture, worldX, worldY, 32f, 32f)
            }
        }

        batch.end()
    }
}
```

---

## Troubleshooting

### Problem: Map doesn't load
**Solution:**
- Check file exists: `app/src/main/assets/maps/aethermoor.tmx`
- Check Logcat: Filter by "TiledMapRenderer"
- Verify TMX file is valid: Open in Tiled, save again

### Problem: Tileset images missing
**Solution:**
- Open `aethermoor.tmx` in text editor
- Find `<tileset>` tag
- Verify `source` attribute is relative path: `../tilesets/ground_tiles.tsx`
- Or verify embedded tileset has correct `<image source="../tilesets/ground_tiles.png"/>`

### Problem: libGDX initialization fails
**Solution:**
- Check all native libraries are added to `build.gradle.kts`
- Clean and rebuild: `./gradlew clean assembleDebug`
- Check minimum SDK is 31 (Android 12) - libGDX requires it

### Problem: Can't see characters on map
**Solution:**
- Verify `updateCharacters()` is being called with current character list
- Implement `renderCharacters()` sprite rendering
- Check character locations match location IDs in your map

---

## Next Steps After Map Is Working

1. **Add more locations** to each region
2. **Create character sprites** for each job class
3. **Add animations** for character movement
4. **Implement location tapping** to show details
5. **Add fog of war** (hide unexplored areas)
6. **Create region transitions** with visual effects
7. **Add map markers** for quests, events, bosses

---

## Resources

### Tiled Documentation
- Official Docs: https://doc.mapeditor.org/
- Working with Objects: https://doc.mapeditor.org/en/stable/manual/objects/
- TMX Map Format: https://doc.mapeditor.org/en/stable/reference/tmx-map-format/

### libGDX Documentation
- Tile Maps: https://libgdx.com/wiki/graphics/2d/tile-maps
- OrthogonalTiledMapRenderer: https://javadoc.io/doc/com.badlogicgames.gdx/gdx/latest/index.html

### Free Assets
- itch.io Tilesets: https://itch.io/game-assets/free/tag-tileset
- OpenGameArt: https://opengameart.org/art-search-advanced?keys=tileset
- Kenney Assets: https://kenney.nl/assets (all CC0 / public domain)

---

## Summary: What You Must Do

### ✅ Required Actions:

1. **Install Tiled Map Editor** from mapeditor.org
2. **Get a tileset** (32x32 recommended) - free from itch.io or create your own
3. **Create the map** in Tiled:
   - 100x100 tiles, orthogonal, 32x32 tile size
   - 5 terrain layers + 1 object layer
   - Add 12+ location points with properties
4. **Save as** `app/src/main/assets/maps/aethermoor.tmx`
5. **Copy tileset PNG** to `app/src/main/assets/tilesets/`
6. **Sync Gradle** in Android Studio
7. **Build and test** on device

### ⚠️ Important Notes:

- The code I created will handle **all the rendering** automatically
- You just need to **create the map file** in Tiled
- The map format is **orthogonal (top-down)**, not isometric
- All camera controls, zooming, panning are **already implemented**
- Character rendering is **stubbed** - you'll need to add sprite loading

---

## File Checklist

When you're done, you should have:

```
app/src/main/
├── assets/
│   ├── maps/
│   │   └── aethermoor.tmx          ✅ Created by you in Tiled
│   └── tilesets/
│       ├── ground_tiles.png         ✅ Your tileset image
│       └── ground_tiles.tsx         ✅ Optional: Tileset definition
├── java/com/watxaut/rolenonplayinggame/
│   └── presentation/map/
│       ├── TiledMapRenderer.kt      ✅ Already created
│       ├── TiledMapView.kt          ✅ Already created
│       └── WorldMapScreen.kt        🔧 You need to integrate
```

---

**Need Help?**
- Check the Troubleshooting section above
- Review Tiled documentation at doc.mapeditor.org
- Check libGDX wiki at libgdx.com/wiki

**Ready to start?** Begin with Phase 1: Install Tiled Map Editor!

---

**Last Updated:** 2025-11-22
**Version:** 1.0
**Status:** Ready for Implementation
