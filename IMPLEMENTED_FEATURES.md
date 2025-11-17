# ROLE NON-PLAYING GAME - IMPLEMENTED FEATURES

**Last Updated**: 2025-11-17

This document provides a comprehensive overview of all implemented features in the Role Non-Playing Game Android application.

---

## 1. CHARACTER SYSTEM

### Character Creation
- **13 Job Classes**: Warrior, Assassin, Rogue, Archer, Mage, Priest, Warlock, Bard, Merchant, Scholar, Paladin, Battle Mage, Ranger
- **6 Core Stats**: Strength (STR), Intelligence (INT), Agility (AGI), Luck (LUK), Charisma (CHA), Vitality (VIT)
- **Stat Allocation**: 10 starting points distributed across stats (minimum 1 per stat)
- **Class-Specific Bonuses**: Each class has primary/secondary stat priorities that influence auto-leveling
- **Character Naming**: Custom name input with validation

### Personality System (Hidden from Player)
- **6 Personality Traits** (0.0-1.0 scale, randomized at creation):
  - **Courage**: Cautious vs brave/reckless behavior
  - **Greed**: Generous vs greedy decision-making
  - **Curiosity**: Focused vs exploratory tendencies
  - **Aggression**: Peaceful vs aggressive combat engagement
  - **Social**: Independent vs social interaction preference
  - **Impulsive**: Methodical vs impulsive actions
- **Job Class Influence**: Personality traits weighted by job class (e.g., Warriors get +courage, Rogues get +curiosity)
- **Risk Tolerance Calculation**: Derived from courage + impulsive - greed, affects decision-making

### Character Progression
- **Experience & Leveling**: Characters gain XP from combat and exploration, level up at threshold (level × 100 XP)
- **Auto-Stat Allocation**: Stats automatically increase on level-up based on job class priorities
  - 2 points to primary stat
  - 2 points to secondary stat
  - 1 point to each other stat
- **HP Scaling**: Max HP = 50 + (vitality × 10) + (level × 5)
- **Full Heal on Level Up**: Character HP fully restored when reaching new level
- **Level Up Animation**: Visual celebration animation displayed in UI

### Character State Management
- **Health Tracking**: Current HP/Max HP with health percentage monitoring
- **Gold System**: Currency accumulation from combat/exploration, spending on healing/equipment
- **Location Tracking**: Current location stored as location ID
- **Discovered Locations**: List of all visited locations
- **Last Active Timestamp**: For offline simulation calculations

---

## 2. AUTONOMOUS AI SYSTEM

### Decision Engine (BasicDecisionEngine)
- **Priority-Based Decision Tree**:
  1. **Survival** (HP < 30%): Flee to safety, heal at inn, or rest
  2. **Critical Needs** (HP < 60% or low gold): Prioritize healing or resource hunting
  3. **Active Quest**: Continue quest objectives
  4. **Idle State**: Explore, combat, shop, or rest based on personality

### Available Decision Types
- **Combat**: Engage enemies in current location
- **Explore**: Travel to new locations to discover areas
- **Rest**: Free healing (30% HP recovery, no cost)
- **Heal at Inn**: Instant full heal for gold cost (10 gold)
- **Flee**: Retreat to safe location (Havenmoor)
- **Shop**: Browse equipment vendors
- **Accept/Continue Quest**: Quest system integration
- **Idle**: Do nothing (brief pause)

### Autonomous Behavior
- **Decision Loop Timing**: 2-8 second intervals between decisions (randomized for unpredictability)
- **Pause/Resume**: AI pauses when navigating away from game screen, resumes on return
- **Real-time Updates**: Character state changes reflect immediately in UI via Kotlin Flow

### Personality-Driven Behavior
- **Exploration Preference**: Curious characters explore more frequently
- **Combat Engagement**: Aggressive characters seek fights more often
- **Shopping Behavior**: Greedy characters browse shops when wealthy
- **Risk-Taking**: High-risk characters more likely to engage difficult enemies

---

## 3. COMBAT SYSTEM

### d21 Dice Mechanic
- **Roll Range**: 1-21 (21-sided die for all uncertain actions)
- **Critical Success (21)**: Automatic success, 2× damage, bonus rewards
- **Critical Failure (1)**: Automatic failure
- **Success Formula**: `Difficulty - Stat - Roll ≤ 0`
- **Luck Stat Effects**:
  - Reroll on critical failure (1) if luck ≥ 10
  - Expanded crit range if luck ≥ 15 (crit on 21 - luck/5)

### Simplified Combat (Offline/Fast Resolution)
- **Single d21 Roll**: One roll determines entire combat outcome
- **Power Calculation**: `(STR + INT + AGI + VIT) / 4 + Level × 2`
- **Combat Score**: `Roll + (CharacterPower - EnemyPower) + LevelBonus`
- **Low-Level Bonus**: +2 combat bonus for levels 1-3 (survival assistance)
- **Outcomes**:
  - Roll 21: WIN (critical success, 1.5× rewards)
  - Score ≥ 12: WIN (standard rewards)
  - Score ≥ 7: FLEE (escape, no rewards)
  - Score < 7: DEATH (respawn, lose 50% gold)

### Detailed Combat (Turn-Based)
- **Attack Roll**: AGI-based to-hit check against difficulty
- **Dodge Roll**: Defender AGI check to avoid damage
- **Damage Calculation**: (STR or INT, whichever higher) × 2 + level/2
- **Damage Reduction**: Vitality/2 reduces incoming damage
- **Critical Hits**: 2× damage multiplier on roll 21
- **Combat Log**: Full round-by-round description in activity log

### Enemy System
- **30+ Enemy Types**: Region-specific enemies with thematic design
- **Level Scaling**: Enemy stats automatically scale to match character level
- **Enemy Tiers**: Normal, Elite, Boss, World Boss, Legendary
- **Heartlands Enemies** (fully implemented):
  - **Levels 1-3**: Slime, Wild Rabbit, Grass Snake
  - **Levels 4-6**: Wolf, Goblin, Bandit
  - **Levels 7-10**: Giant Spider, Skeleton, Rogue Mage

### Combat Rewards
- **Experience Points**: Base = enemy level × 10, with multiplier for level difference
- **Gold**: Base = enemy level × 5, randomized (0.8-1.5× multiplier)
- **Equipment Drops**: 10% total drop rate (9% common tier, 1% rare tier)

---

## 4. WORLD & EXPLORATION

### Regions (5 Total)
- **Heartlands** (Levels 1-10): Safe starting area with grasslands and settlements
- **Thornwood Wilds** (Levels 11-20): Dense forests with dangerous wildlife
- **Ashenveil Desert** (Levels 21-30): Harsh desert environment
- **Frostpeak Mountains** (Levels 31-40): Snow-covered peaks with extreme conditions
- **Stormcoast Reaches** (Levels 41-50): Treacherous coastal region with storms

### Locations (Heartlands Fully Implemented)
- **Havenmoor** (starting town): Safe town with inn, shop, quest givers
- **Meadowbrook Fields**: Level 1-3 training ground with basic enemies
- **Whispering Grove**: Forest area with ancient standing stones
- **Miller's Rest**: Small settlement with merchant
- **Broken Bridge**: Exploration area with moderate danger

### Exploration Mechanics
- **Visit Actions** (70% probability): Simple sightseeing, no material rewards
- **Loot Exploration** (30% probability):
  - **Excellent** (1.5%): 50-100 gold, 50 XP
  - **Good** (6%): 20-40 gold, 25 XP
  - **Poor** (18%): 5-15 gold, 10 XP
  - **Nothing** (4.5%): 5 XP only
- **Location Discovery**: New locations added to character's discovered list
- **Autonomous Movement**: Characters travel between connected locations based on AI decisions

---

## 5. MISSION SYSTEM

### Principal Missions
- **36 Total Missions**: 3 unique missions per job class (13 classes)
- **Mission Structure**:
  - 4+ story steps per mission (2% discovery chance during gameplay)
  - Boss battle after all steps complete (2% encounter chance)
  - Lore fragments revealed at each step and boss defeat
- **Example Missions**:
  - **Warrior**: "The Ironhide Challenge" (hunt Ancient Boar boss)
  - **Warrior**: "The Frozen Halls" (explore dwarven mystery)
  - **Mage**: "The Crystal Tower" (unlock arcane secrets)
  - *(All 36 missions defined in PrincipalMissionsRepository)*
- **Win Conditions**: Level requirements, stat thresholds, boss defeat
- **Rewards**: XP, gold, guaranteed items, rare item chance (varies by mission)
- **Lore Integration**: Each mission reveals world mysteries from WORLD_LORE.md

### Secondary Missions
- **100 Total Missions**: Varied side quests with diverse objectives
- **Discovery Rate**: 1% chance during any action
- **Multiple Active**: Characters can have several active simultaneously
- **Mission Categories**:
  - **Exploration**: Visit specific locations
  - **Collection**: Defeat certain enemy types
  - **Combat**: Battle challenges and combat trials
  - **Social**: NPC interactions and relationship building
  - **Survival**: Endurance and survival tests
  - **Luck**: Luck-based challenges and gambling
  - **Achievement**: Meta goals and milestones
- **Win Condition Types** (10 variations):
  - Visit location, collect items, defeat enemies
  - Reach level, accumulate gold, equip rarity tier
  - Use stat threshold, survive days, critical hits, trade with NPCs
- **Rewards**: Always XP + gold, 20% equipment chance, 2% rare equipment chance

### Mission Progress Tracking
- **Database Tables**: mission_progress (principal), secondary_mission_progress (side quests)
- **Step Completion**: Tracked via completed_steps set
- **Boss Tracking**: Encounter status and defeat tracking
- **Progress Percentage**: Displayed in Missions tab UI
- **Supabase Integration**: Real-time sync with backend database

---

## 6. EQUIPMENT & INVENTORY SYSTEM

### Equipment Slots (6 Total)
- **Weapon Main**: Primary weapon (all classes)
- **Weapon Off-Hand**: Secondary weapon (Assassin, Rogue, Ranger can dual-wield)
- **Armor**: Body armor (chest piece)
- **Gloves**: Hand armor
- **Head**: Helmet/hat
- **Accessory**: Ring/necklace

### Equipment Stats & Bonuses
- **Stat Bonuses**: Each item provides bonuses to STR, INT, AGI, VIT, LUK, CHA
- **Total Stats Calculation**: Character base stats + equipment bonuses = displayed stats
- **Class Affinity**: Items have preferred class for tie-breaking in auto-equip logic

### Rarity Tiers (5 Levels)
- **Common** (9% drop rate): White color, basic stats
- **Uncommon** (5% drop rate): Green color, improved stats
- **Rare** (1% drop rate): Blue color, strong stats
- **Epic** (0.5% drop rate): Purple color, powerful stats
- **Legendary** (0.1% drop rate): Gold color, exceptional stats

### Equipment Database
- **100+ Equipment Items**: 20+ items per slot with varied stats
- **Auto-Equip Logic**: Compares total stat bonuses, prefers class affinity, prioritizes new items
- **Example Items**:
  - **Rusty Sword** (+2 STR) - Common starter weapon
  - **Wooden Staff** (+2 INT, Mage) - Basic caster weapon
  - **Iron Sword** (+4 STR, Warrior) - Upgraded warrior weapon
  - **Apprentice's Wand** (+3 INT, Mage) - Entry-level magic focus

### Inventory System
- **Structure**: List of item IDs stored in character data
- **Current State**: Basic structure implemented, full system planned for future

---

## 7. OFFLINE SIMULATION SYSTEM

### Time Compression
- **Compression Ratio**: 1 real hour offline = 2 game hours simulated
- **Maximum Cap**: 7 days (1008 game hours = 168 real hours offline)
- **Minimum Threshold**: Requires 1+ minute offline to trigger simulation

### Simulation Backend (Edge Function)
- **Technology**: TypeScript Deno Edge Function on Supabase
- **Modular Components**:
  - **dice.ts**: d21 roll implementation with luck effects
  - **combat.ts**: Simplified combat resolution
  - **ai.ts**: Decision-making logic
  - **leveling.ts**: XP calculation and level-up handling
  - **simulator.ts**: Main simulation loop orchestration

### Simulation Activities
- **Combat Encounters**: Probability-based battles with enemies
- **Exploration**: Location discovery and loot finding
- **Resting**: HP recovery over time
- **Leveling**: Automatic stat allocation on level-up
- **Death/Respawn**: Gold penalty and respawn in safe location

### Simulation Summary Output
- **Statistics Tracked**:
  - Total combats (won/lost/fled)
  - XP gained, gold gained/lost
  - Levels gained
  - Deaths
  - Locations discovered
  - Items found
  - Major events (boss encounters, mission progress)
- **Mission Progress**: Principal mission steps discovered, secondary missions unlocked, lore discoveries
- **Character State Update**: Level, XP, gold, HP synchronized after simulation completes

### Client-Side Integration
- **OfflineSimulationManager**: Kotlin class managing simulation state
- **Network Detection**: Checks connectivity before attempting simulation
- **Retry Logic**: Exponential backoff (1s, 2s, 4s delays) for up to 3 attempts
- **Background Recording**: Timestamp saved when app goes to background
- **Foreground Check**: Simulation triggered automatically when app returns to foreground

---

## 8. UI/UX FEATURES

### Screens Implemented (10 Total)
1. **LoginScreen**: Email/password authentication via Supabase
2. **ProfileScreen**: User info display, sign-out button
3. **HomeScreen**: Character list, create new character button
4. **CharacterCreationScreen**: Job selection, name input, stat allocation interface
5. **GameScreen**: Real-time autonomous gameplay observation
6. **WorldMapScreen**: World overview with region display
7. **LeaderboardScreen**: All characters ranked by level then XP
8. **ObjectsScreen**: View items and equipment
9. **OfflineSummaryScreen**: Post-simulation activity summary with statistics
10. **MainScreen**: Bottom navigation hub

### Game Screen Tabs (5)
- **Current Tab**: Character stats, HP bar, level, XP bar, current action display
- **Equipment Tab**: Equipped items in all 6 slots, stat bonuses shown
- **Lore Tab**: Discovered lore entries with category badges
- **Missions Tab**: Principal mission progress with step tracking
- **Activity Log Tab**: Last 50 actions with timestamps and categorization

### Bottom Navigation (5 Tabs)
- **Heroes**: Character list and creation
- **Leaderboard**: Global character rankings (level → XP)
- **Objects**: Item/equipment viewer
- **Map**: World map overview
- **Profile**: User account management

### Visual Elements
- **Health Bar**: Linear progress indicator for HP (green/yellow/red coloring)
- **XP Progress Bar**: Shows progress to next level with percentage
- **Level Up Animation**: Celebration animation on level-up event
- **Activity Log**: Real-time scrolling feed with categorized icons
- **Current Action Display**: Shows what character is currently doing in real-time
- **Timestamp Formatting**: Human-readable time display (e.g., "2 minutes ago")
- **Scrollable Tab Bar**: Horizontal scrolling prevents text wrapping
- **2-Row Stats Layout**: 6 stats displayed in 2 rows of 3, left-aligned with spacing

### Activity Log Features
- **Categorization**: Combat, exploration, rest, shopping, level-up, death, loot, travel, idle
- **Color Coding**: Major events (level-up, death) highlighted
- **Rewards Display**: XP and gold amounts shown in activity descriptions
- **Combat Details**: Roll values, outcomes, enemy names included
- **Loot Messages**: Varied humorous messages via MessageProvider
- **Auto-Scroll**: Newest activities appear at top
- **Limit**: Last 50 activities displayed (database stores last 500)

---

## 9. BACKEND INTEGRATION (Supabase)

### Authentication
- **Supabase Auth**: Email/password login system
- **Session Management**: JWT token handling with automatic refresh
- **Row Level Security (RLS)**: Users can only access their own character data
- **Anonymous Auth Support**: Optional for testing purposes

### Database Tables (PostgreSQL)
1. **characters**: All character data (stats, location, personality, inventory, equipment)
2. **activity_logs**: Character activity history (last 500 entries per character)
3. **encounters**: Social interactions between characters
4. **active_locations**: Character proximity for social matching
5. **achievements**: Unlocked achievements per character
6. **mission_progress**: Principal mission tracking (steps, boss status)
7. **secondary_mission_progress**: Side quest tracking
8. **lore_discoveries**: Unlocked lore entries with discovery source

### Leaderboard Views (Materialized)
- **leaderboard_levels**: Top 100 characters by level then XP
- **leaderboard_wealth**: Top 100 characters by gold
- **Refresh Policy**: Periodically refreshed, manual refresh available in UI

### Edge Functions
- **offline-simulation**: Processes offline character progression
  - **Inputs**: character ID, JWT authentication token
  - **Outputs**: Simulation summary, updated character state, activity log entries
  - **Security**: User authentication required, RLS enforced on all queries

### Data Synchronization
- **Real-Time Updates**: Character state updates via Kotlin Flow + Coroutines
- **Activity Logs**: Inserted in real-time during gameplay, streamed to UI
- **Mission Progress**: Saved to Supabase after discoveries
- **Leaderboard**: Fetched on-demand with refresh capability

---

## 10. LORE & WORLD BUILDING

### Lore Categories (7)
- **World History**: Origins and major historical events
- **Regions of Aethermoor**: Geographic and cultural information
- **Factions & Settlements**: Organizations and communities
- **Mysteries**: Unexplained phenomena and secrets
- **Legendary Creatures**: Mythical beings and bosses
- **Hero's Journey**: Personal character narratives
- **Artifacts & Relics**: Magical items and ancient objects

### Predefined Lore Entries
- **Initial Lore** (unlocked on character creation):
  - "The Island of Aethermoor" - World overview
  - "The Calling" - Mystery of character summoning
- **Regional Lore**: 5 region descriptions (unlocked on first visit)
- **Mission-Linked Lore**: Revealed through principal mission steps and boss battles

### Lore Discovery Sources
- **Principal Mission Steps**: Story progression reveals lore
- **Boss Battles**: Defeating bosses unlocks their lore
- **Location Discoveries**: Visiting new locations
- **Item Acquisitions**: Finding legendary items
- **NPC Interactions**: Conversations with lore keepers

### Lore Display UI
- **Lore Tab**: Located in GameScreen tabs
- **Display Elements**:
  - Category badge with color coding
  - Lore title (bold)
  - Lore content (italicized)
  - Discovery source (dynamic from enum)
  - Timestamp of discovery

---

## 11. ADDITIONAL SYSTEMS

### Message Provider
- **Dynamic Messages**: 100+ varied messages for different action types
- **Message Categories**: Combat (win/flee/death), exploration, loot finding, rest
- **Tone Balance**: 70% serious, 30% humor (per TONE_AND_STYLE_GUIDE.md)
- **Randomization**: Prevents repetitive text, keeps activity log fresh

### Achievements System
- **Achievement Types**: Kill count, level milestones, gold earned, location discovery, quest completion, boss defeats, item collection, deaths
- **Database Ready**: Table structure in place for tracking
- **Tiers**: Bronze, Silver, Gold, Platinum, Diamond
- **Categories**: Combat, exploration, social, progression, collection, special
- **Current State**: Database structure ready, UI implementation pending

### Game Balance
- **Level Curve**:
  - Fast (Levels 1-10): Quick progression for engagement
  - Moderate (Levels 11-30): Steady advancement
  - Slow (Levels 31-50): Endgame grind
- **Difficulty Scaling**: Region-based (Heartlands easiest → Stormcoast hardest)
- **Combat Balance**: Low-level bonus (+2) for levels 1-3 to improve early survival
- **Death Penalty**: 50% gold loss, respawn in safe town (Havenmoor)
- **Offline Cap**: Maximum 7 days to encourage active play sessions

### NPC System
- **NPC Types**: Merchant, quest giver, lore keeper, innkeeper, trainer, guard, commoner, mysterious
- **Personalities**: Friendly, grumpy, mysterious, greedy, helpful, hostile, neutral, eccentric
- **Current State**: Database defined, social interaction system pending

---

## SUMMARY STATISTICS

### Content Volume
- **Job Classes**: 13
- **Core Stats**: 6
- **Regions**: 5
- **Locations**: ~20 (Heartlands fully implemented)
- **Principal Missions**: 36 (3 per class)
- **Secondary Missions**: 100
- **Enemy Types**: 30+ (Heartlands complete)
- **Equipment Items**: 100+ (20+ per slot)
- **Rarity Tiers**: 5
- **Lore Entries**: 20+ predefined
- **Decision Types**: 9
- **Activity Types**: 10
- **UI Screens**: 10
- **Database Tables**: 8

### Technical Architecture
- **Language**: Kotlin
- **Min SDK**: 31 (Android 12)
- **Target SDK**: 36 (Android 15)
- **Backend**: Supabase (PostgreSQL + Edge Functions)
- **Auth**: Supabase Auth with JWT tokens
- **Real-Time**: Kotlin Flow + Coroutines
- **UI Framework**: Jetpack Compose
- **Dependency Injection**: Hilt/Dagger
- **Local Storage**: Room Database (cache)
- **Edge Functions**: Deno + TypeScript

---

## CORE GAME LOOP

1. **Character Creation**: Player creates character with job class, stats, name
2. **Initial Setup**: Character spawns in Havenmoor with 2 initial lore entries
3. **Autonomous AI**: Character makes decisions every 2-8 seconds based on personality and situation
4. **Actions Executed**: Combat, exploration, rest, healing, shopping
5. **Progression**: Gain XP, level up, auto-allocate stats, find equipment
6. **Mission Discovery**: Unlock principal mission steps and secondary missions
7. **Lore Unlocking**: Discover world lore through missions and exploration
8. **Offline Simulation**: When app closed, time compresses 2:1 and simulation runs on server
9. **Return Summary**: Player sees summary of offline activities when returning
10. **Observation**: Player watches character live independent life, cannot control directly

---

## DESIGN PHILOSOPHY

**Players observe, characters act.** This game is built on the principle of autonomous characters living independent lives. Players create characters and watch them make their own decisions, fight their own battles, and write their own stories. The AI, personality system, and d21 dice mechanic combine to create emergent, unpredictable narratives that are unique to each character.

---

*This document reflects all implemented features as of 2025-11-17. Features marked as "pending" or "planned" have partial implementation or database structure but incomplete functionality.*
