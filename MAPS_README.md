# Map Assets Directory Structure

This document describes the expected directory structure for Tiled map assets.

## Directory Structure

```
app/src/main/assets/
├── maps/
│   └── aethermoor.tmx          # Main world map (create in Tiled Map Editor)
├── tilesets/
│   ├── ground_tiles.png        # Ground terrain tileset image
│   ├── ground_tiles.tsx        # Tileset definition (optional)
│   ├── decorations.png         # Decoration tileset (optional)
│   └── structures.png          # Building/structure tileset (optional)
└── sprites/
    └── characters/
        ├── warrior.png         # Character sprites (future)
        ├── mage.png
        └── ...
```

## Creating the Assets Directory

The `assets` directory doesn't exist by default in Android projects. Create it:

1. **In Android Studio:**
   - Right-click on `app/src/main/`
   - Select: **New → Directory**
   - Enter: `assets`
   - Click OK

2. **Create subdirectories:**
   - `assets/maps/`
   - `assets/tilesets/`
   - `assets/sprites/` (optional, for future use)

## Map File: aethermoor.tmx

**Location:** `app/src/main/assets/maps/aethermoor.tmx`

**How to Create:**
1. Install Tiled Map Editor from https://www.mapeditor.org/
2. Create new map:
   - Orientation: **Orthogonal** (not isometric!)
   - Map size: 100x100 tiles
   - Tile size: 32x32 pixels
3. Add your tilesets
4. Paint the five regions of Aethermoor
5. Add location objects
6. Save as: `aethermoor.tmx`

**See:** `TILEMAP_IMPLEMENTATION_GUIDE.md` for complete instructions

## Tileset Images

**Location:** `app/src/main/assets/tilesets/`

**Required:**
- At least one tileset PNG file (e.g., `ground_tiles.png`)
- Recommended size: 512x512 pixels (16x16 grid of 32x32 tiles)
- Format: PNG with transparency support

**Where to Get:**
- **Free:** itch.io, OpenGameArt.org, Kenney.nl
- **Create:** Use Aseprite, GIMP, or any pixel art tool
- **Purchase:** itch.io, GraphicRiver

**Example Tileset Layout (16x16 grid):**
```
Row 0: Grass variations (light, medium, dark, with flowers)
Row 1: Sand variations (light sand, dark sand, sand with rocks)
Row 2: Snow variations (pure snow, snow with ice, snow with rocks)
Row 3: Stone/rock variations
Row 4: Water variations (still water, flowing water)
Row 5: Path variations (cobblestone, dirt path)
Rows 6-15: Decorations, transitions, special tiles
```

## Tileset Definition: .tsx files

**Location:** `app/src/main/assets/tilesets/ground_tiles.tsx`

**Optional but recommended:** Tiled can create external tileset definitions

**Benefits:**
- Reuse tilesets across multiple maps
- Store tile properties in one place
- Easier to update tile metadata

## Integration with Code

The `TiledMapRenderer` class automatically:
1. Looks for maps in `assets/maps/`
2. Loads tilesets referenced by the TMX file
3. Resolves relative paths from the map file

**Example map loading:**
```kotlin
// In your code:
TiledMapView(
    mapPath = "maps/aethermoor.tmx",  // Automatically looks in assets/
    // ...
)
```

## File Size Considerations

**Android APK Size:**
- Each PNG tileset: ~50-200 KB (compressed)
- TMX map file: ~10-50 KB (text-based)
- Total for basic setup: < 500 KB

**Optimization Tips:**
1. Use PNG compression (OptiPNG, TinyPNG)
2. Avoid huge tilesets (keep under 2048x2048)
3. Use texture atlases for multiple small images
4. Enable resource shrinking in release builds

## Verifying Assets Exist

**In Android Studio:**
1. Switch to "Project" view (not "Android" view)
2. Navigate to `app/src/main/assets/`
3. Verify files are present

**At runtime:**
- Check Logcat for "TiledMapRenderer" errors
- Map loader will log if files are missing

## Common Issues

### Issue: Map doesn't load
**Solution:**
- Verify `aethermoor.tmx` exists in `assets/maps/`
- Check file name is exact (case-sensitive)
- Clean and rebuild project

### Issue: Tileset images missing (black tiles)
**Solution:**
- Open TMX file in Tiled
- Check tileset source paths are relative
- Verify PNG files exist in `assets/tilesets/`

### Issue: Can't find assets directory
**Solution:**
- Assets go in `app/src/main/assets/` (not `app/assets/`)
- Must be at same level as `java/` and `res/`

## Next Steps

1. ✅ Create the directory structure above
2. ✅ Download or create a tileset PNG
3. ✅ Install Tiled Map Editor
4. ✅ Create `aethermoor.tmx` following the guide
5. ✅ Test in your app

**See full instructions:** `TILEMAP_IMPLEMENTATION_GUIDE.md`

---

**Last Updated:** 2025-11-22
