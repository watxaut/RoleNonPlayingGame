# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Role Non-Playing Game** is an Android game where players observe autonomous characters living independent lives in an RPG world. Players create characters but don't control them - instead, they watch as their characters make decisions, explore, fight, level up, and interact with other characters entirely on their own.

## Key Design Documents

Before implementing features, review these foundational documents:

1. **FUNCTIONAL_DESIGN_DOCUMENT.md** - Complete game mechanics, systems, and behavior specifications
   - Character creation system (stats, job classes, personality traits)
   - **d21 dice mechanic** (section 3.2): All actions use a 21-sided die roll system
     - Roll 21 = automatic critical success
     - Roll 1 = automatic critical failure
     - Rolls 2-20 = success/failure based on character stats vs. difficulty
   - Autonomous AI behavior and decision-making framework
   - Combat, exploration, quest, and social systems
   - Progression, loot, and achievement systems

2. **WORLD_LORE.md** - The Island of Aethermoor setting
   - Five distinct regions (Heartlands, Thornwood Wilds, Ashenveil Desert, Frostpeak Mountains, Stormcoast Reaches)
   - Legendary monsters and bosses for each region
   - World mysteries, factions, and lore structure
   - Designed to be scalable for future chapters

3. **TONE_AND_STYLE_GUIDE.md** - Writing style and tone guidelines
   - 70% serious / 30% humor balance
   - Item description guidelines (legendary items are serious, common/junk items can be humorous)
   - NPC dialogue patterns and personality types
   - Easter egg and reference guidelines (Final Fantasy, D&D, classic RPGs)
   - Achievement naming conventions and examples

## Game Design Core Principles

When implementing features, adhere to these principles:

1. **Autonomy is Absolute**: Characters make ALL decisions - players can never directly control them
2. **Observation Over Action**: Player engagement is through watching, not doing
3. **Emergent Storytelling**: Each character's journey should be unique based on personality + stats + dice rolls
4. **The d21 System**: All uncertain actions MUST use the 21-sided die mechanic (see FUNCTIONAL_DESIGN_DOCUMENT.md section 3.2)
5. **Tone Balance**: Keep the world grounded and serious, but allow humor through items, NPCs, and situations

## Android Project Structure

**Technology Stack:**
- Language: Kotlin
- Target SDK: 36 (Android 15)
- Min SDK: 31 (Android 12)
- Build System: Gradle with Kotlin DSL
- Package: `com.watxaut.rolenonplayinggame`

**Build Commands:**
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests "com.watxaut.rolenonplayinggame.ExampleUnitTest"

# Clean build
./gradlew clean
```

**Test Commands:**
```bash
# Run all unit tests with coverage
./gradlew testDebugUnitTest

# Run specific test method
./gradlew test --tests "com.watxaut.rolenonplayinggame.ExampleUnitTest.addition_isCorrect"

# Run instrumented tests on connected device
./gradlew connectedDebugAndroidTest
```

## Architecture Guidelines (To Be Implemented)

The game will require these major architectural components:

### 1. Character System
- Character creation and customization
- Stat management (STR, INT, AGI, LUK, CHA, VIT)
- Job class implementations (12 classes: Warrior, Assassin, Rogue, Archer, Mage, Priest, Warlock, Bard, Merchant, Scholar, Paladin, Battle Mage, Ranger)
- Personality trait system (hidden traits that influence decisions)
- Leveling and skill point auto-allocation

### 2. Dice Roll System (d21)
- Core mechanic that resolves all uncertain actions
- Must implement:
  - Roll generation (1-21)
  - Critical success/failure handling (21 and 1)
  - Success calculation for rolls 2-20: `Difficulty - Stat - Roll ≤ 0`
  - Luck stat special effects (reroll on 1, expanded crit range)
- Used for: combat, social interactions, trap detection, loot quality, exploration events

### 3. Autonomous AI Decision Engine
- Priority-based decision tree (see FUNCTIONAL_DESIGN_DOCUMENT.md section 3.1)
- Behavior driven by: stats, job class, personality traits, current situation
- Decision frequency: 3-10 seconds during active play
- Must feel unpredictable yet consistent with character personality

### 4. World & Location System
- Five-region world map (levels 1-50 content)
- Location discovery and fog-of-war
- Dynamic event spawning
- Monster and NPC population

### 5. Combat System
- Autonomous engagement decisions based on aggression score
- d21 rolls for attacks and defense
- Job-class-specific ability usage
- Health-based retreat logic

### 6. Activity Log & Player UI
- Real-time action logging
- Offline simulation summary ("While you were away...")
- Character stats display
- Inventory view (read-only for player)
- Achievement tracking

### 7. Offline Simulation
- Time compression: 1 real hour = 6 game hours offline
- Simplified probability-based combat
- Activity generation with timestamps
- State persistence

### 8. Social/Multiplayer (Future)
- Character encounters with other players' characters
- Autonomous interaction negotiation (trading, parties, combat, ignore)
- Server-based character synchronization

## Testing Philosophy

- Use **pytest** for Python unit tests (if backend is added)
- Use **JUnit** for Kotlin unit tests
- Test autonomous decision logic thoroughly - characters should behave consistently with their personalities
- Test dice roll distribution and edge cases (natural 1s and 21s)
- Mock/stub random elements to ensure deterministic test results

## Content Creation Guidelines

When adding items, quests, NPCs, or monsters:

1. **Check tone in TONE_AND_STYLE_GUIDE.md** - match the appropriate tier (serious for legendary, playful for common)
2. **Verify lore consistency with WORLD_LORE.md** - ensure content fits the region and world
3. **Follow naming conventions** - see TONE_AND_STYLE_GUIDE.md for NPC, item, and monster naming
4. **Include dice rolls where appropriate** - uncertain outcomes should use d21 system
5. **Consider character autonomy** - content should be discoverable/usable by AI, not requiring player input

## Game Balance Considerations

- Level curve: Fast (1-10), Moderate (11-30), Slow (31-50)
- Difficulty scaling by region (Heartlands easiest → Stormcoast hardest)
- All personality types should be viable - no "optimal" personality
- Luck builds should feel exciting due to critical hit potential
- Offline progression capped to encourage active play sessions

## Future Expansion Points (Post-MVP)

Documented in design docs but not initial implementation:
- Crafting system
- Housing system
- Pets/Companions
- Prestige/Rebirth system
- Guild system
- Dynamic quest generation
- World events
- Additional regions beyond the initial five

## Important Notes

- The main source directory (`app/src/main/java/com/watxaut/rolenonplayinggame/`) is currently empty - this is a fresh project
- All game design is documented but implementation is pending
- When implementing, start with core systems: Character → Dice Rolls → AI Decision Framework → World
- Maintain the design philosophy: **players observe, characters act**
