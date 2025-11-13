# Role Non-Playing Game - Technical Implementation Document

**Version:** 1.0
**Date:** 2025-11-13
**Status:** Proposal - Three Architecture Options

---

## Executive Summary

This document presents three architectural options for implementing **Role Non-Playing Game**, analyzing each approach's technical feasibility, cost implications, scalability, and alignment with game requirements.

**Key Technical Challenges:**
1. **Autonomous AI Decision-Making** - Characters need sophisticated, personality-driven AI
2. **Social/Multiplayer Features** - Character-to-character interactions across devices
3. **Offline Simulation** - Server-side processing when players are offline
4. **Budget Constraints** - Need cost-effective solution, ideally free tier initially
5. **Scalability** - Support growth from MVP to thousands of concurrent users

**Recommendation:** **Option 2 - Hybrid Architecture with Supabase** (detailed analysis below)

---

## Table of Contents

1. [Requirements Analysis](#requirements-analysis)
2. [Option 1: Local-First Architecture](#option-1-local-first-architecture)
3. [Option 2: Hybrid Architecture with Supabase (RECOMMENDED)](#option-2-hybrid-architecture-with-supabase)
4. [Option 3: Cloud-Native with AI Services](#option-3-cloud-native-with-ai-services)
5. [Comparison Matrix](#comparison-matrix)
6. [Recommended Architecture (Option 2) - Detailed Design](#recommended-architecture-detailed-design)
7. [Implementation Roadmap](#implementation-roadmap)
8. [Cost Breakdown](#cost-breakdown)
9. [Risk Analysis](#risk-analysis)

---

## Requirements Analysis

### Functional Requirements

#### Core Systems
1. **Character System**
   - Character creation with stat allocation
   - 12 job classes with unique behaviors
   - Hidden personality traits (6 dimensions)
   - Autonomous stat allocation on level-up

2. **Dice Roll System (d21)**
   - RNG generation (1-21)
   - Success calculation: `Difficulty - Stat - Roll ≤ 0`
   - Critical success/failure handling
   - Luck stat special effects

3. **Autonomous AI Engine**
   - Priority-based decision tree (8 priority levels)
   - Decision frequency: 3-10 seconds during active play
   - Context-aware behavior (personality, stats, situation)
   - Deterministic but varied outcomes

4. **World & Location System**
   - 5 regions (Heartlands, Thornwood, Ashenveil, Frostpeak, Stormcoast)
   - Dynamic event spawning
   - Fog-of-war discovery system
   - Monster and NPC population

5. **Combat System**
   - Autonomous engagement decisions
   - d21-based attack/defense resolution
   - Job-class-specific abilities
   - Health-based retreat logic

6. **Activity Logging**
   - Real-time action logging (last 500 entries)
   - Offline summary generation
   - Event categorization and filtering
   - Timestamp management

7. **Offline Simulation**
   - Time compression: 1 real hour = 6 game hours
   - Simplified combat (probability-based)
   - Activity generation with timestamps
   - State persistence

8. **Social/Multiplayer Features**
   - Character encounters in shared locations
   - Autonomous interaction negotiation
   - Trading, party formation, combat, ignore options
   - Public character profiles and leaderboards

### Non-Functional Requirements

1. **Performance**
   - AI decision-making: < 100ms per decision
   - Offline simulation: Process 6+ hours in < 5 seconds on backend
   - UI responsiveness: 60 FPS for animations
   - Network latency: < 500ms for social features

2. **Scalability**
   - MVP: 100-1,000 concurrent users
   - Growth: 10,000+ concurrent users
   - Offline simulation: Handle 24+ hours of inactivity

3. **Data Storage**
   - Character data: ~10-50 KB per character
   - Activity logs: ~5-20 KB per character (500 entries)
   - World state: ~100-500 KB (shared)
   - Total per user: ~20-100 KB

4. **Availability**
   - Offline mode: Full functionality without network
   - Server uptime: 99%+ (for social features)
   - Data sync: Eventual consistency acceptable

5. **Cost**
   - MVP budget: $0-50/month
   - Growth stage: < $500/month for 10K users
   - Per-user cost: < $0.05/month at scale

---

## Option 1: Local-First Architecture

### Overview

Maximize on-device processing with minimal backend infrastructure. AI runs entirely on Android device, backend only handles social features and data sync.

### Architecture Diagram

```
┌─────────────────────────────────────────┐
│         Android Application             │
├─────────────────────────────────────────┤
│  UI Layer (Jetpack Compose)             │
├─────────────────────────────────────────┤
│  AI Decision Engine (Kotlin)            │
│  - Rule-based decision tree             │
│  - Personality evaluation               │
│  - Combat simulation                    │
│  - Offline simulation runner            │
├─────────────────────────────────────────┤
│  Game Logic Layer                       │
│  - Character management                 │
│  - d21 dice system                      │
│  - World state management               │
│  - Activity log generation              │
├─────────────────────────────────────────┤
│  Data Layer (Room Database)             │
│  - Character data                       │
│  - Activity logs                        │
│  - World discovery state                │
└─────────────────────────────────────────┘
           │
           │ Network (Social only)
           ▼
┌─────────────────────────────────────────┐
│     Minimal Backend (Firebase)          │
├─────────────────────────────────────────┤
│  Firestore Database                     │
│  - Public character profiles            │
│  - Active location registry             │
│  - Encounter matchmaking queue          │
├─────────────────────────────────────────┤
│  Cloud Functions (Minimal)              │
│  - Encounter coordination               │
│  - Leaderboard updates                  │
└─────────────────────────────────────────┘
```

### Technology Stack

**Client (Android)**
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM with Clean Architecture
- **Database:** Room (SQLite)
- **DI:** Hilt
- **Coroutines:** Kotlin Coroutines + Flow
- **Background Work:** WorkManager (offline simulation)

**Backend**
- **Platform:** Firebase (Free Spark Plan)
- **Database:** Firestore (1 GB storage, 50K reads/day)
- **Functions:** Cloud Functions (125K invocations/month free)
- **Auth:** Firebase Auth (optional, anonymous auth)

### AI Implementation (Rule-Based)

**Decision Engine Design:**

```kotlin
sealed class Decision {
    data class Combat(val target: Enemy, val strategy: CombatStrategy) : Decision()
    data class Movement(val destination: Location, val reason: MovementReason) : Decision()
    data class Social(val character: OtherCharacter, val interaction: InteractionType) : Decision()
    data class Rest(val duration: Duration) : Decision()
    // ... other decision types
}

class AutonomousAI(
    private val character: Character,
    private val worldState: WorldState,
    private val personalityTraits: PersonalityTraits
) {
    fun makeDecision(context: DecisionContext): Decision {
        // Priority-based decision tree
        return when {
            isSurvivalThreatened() -> handleSurvival()
            hasCriticalNeeds() -> handleCriticalNeeds()
            hasActiveQuest() -> continueQuest()
            hasOpportunity() -> evaluateOpportunity()
            needsResources() -> gatherResources()
            shouldExplore() -> exploreWorld()
            hasNearbyCharacters() -> evaluateSocialInteraction()
            else -> idleBehavior()
        }
    }

    private fun isSurvivalThreatened(): Boolean {
        return character.healthPercent < 30f
    }

    private fun evaluateOpportunity(): Decision {
        // Personality-driven evaluation
        val riskTolerance = personalityTraits.courage - personalityTraits.caution
        val opportunity = context.availableOpportunities.firstOrNull() ?: return idle()

        // Calculate risk vs reward with personality modifier
        val acceptChance = calculateChance(opportunity.reward, opportunity.risk, riskTolerance)

        return if (d21.roll() + acceptChance >= 15) {
            opportunity.toDecision()
        } else {
            idle()
        }
    }
}
```

**Offline Simulation:**

```kotlin
class OfflineSimulator(
    private val ai: AutonomousAI,
    private val character: Character
) {
    suspend fun simulate(offlineMinutes: Long): SimulationResult {
        val gameHours = offlineMinutes / 10 // 1 real hour = 6 game hours
        val activities = mutableListOf<Activity>()

        var currentTime = character.lastActiveTime

        repeat(gameHours.toInt()) {
            // Simplified decision per game hour
            val decision = ai.makeDecisionOffline()
            val outcome = resolveDecisionProbabilistically(decision)

            activities.add(Activity(
                timestamp = currentTime,
                type = outcome.type,
                description = outcome.description,
                rewards = outcome.rewards
            ))

            currentTime = currentTime.plusHours(1)
        }

        return SimulationResult(
            activities = activities,
            finalState = character.currentState
        )
    }
}
```

### Social Features Implementation

**Encounter Coordination:**

1. **Location Broadcasting:**
   - Client periodically updates character's current location to Firestore
   - Only active players (app open) are visible
   - Location data TTL: 30 seconds

2. **Encounter Matching:**
   - Client queries Firestore for other characters in same location
   - Cloud Function coordinates encounter between two AIs
   - Both clients receive encounter data and simulate autonomously
   - Results synced back to both devices

**Firebase Structure:**

```
/active_characters/{characterId}
  - location: string
  - level: number
  - jobClass: string
  - lastUpdate: timestamp
  - publicProfile: object

/encounters/{encounterId}
  - participants: [characterId1, characterId2]
  - location: string
  - status: "initiated" | "negotiating" | "completed"
  - outcome: object (populated after resolution)

/leaderboards/
  - byLevel: sorted list
  - byAchievements: sorted list
  - byWealth: sorted list
```

### Advantages

1. **Cost:** Nearly free (Firebase Spark plan covers MVP)
2. **Offline-First:** Full functionality without network
3. **Privacy:** Character data stays on device
4. **Performance:** No network latency for AI decisions
5. **Simplicity:** Minimal backend complexity

### Disadvantages

1. **Limited Multiplayer:** Social features are basic
2. **No True Offline Simulation:** Simulation runs on device when app reopens
3. **Device Performance:** AI complexity limited by mobile hardware
4. **Cheating Risk:** Client-side logic vulnerable to modification
5. **Scalability:** Firebase free tier limited (need to upgrade eventually)

### Cost Estimate

**MVP (100-1K users):**
- Firebase Spark Plan: **$0/month**
- Total: **$0/month**

**Growth (10K users):**
- Firebase Blaze Plan:
  - Firestore: ~$0.18/10K reads, ~$0.54/10K writes
  - Estimate: 1M reads/day, 100K writes/day = ~$18/month + $1.6/month
  - Cloud Functions: ~125K invocations/month free, overage $0.40/million
  - Estimate: 500K invocations/month = ~$0.15/month
- Total: **~$20/month**

---

## Option 2: Hybrid Architecture with Supabase

### Overview

Balance client and server processing. Complex AI runs on device, but server handles offline simulation, social coordination, and data persistence. Uses Supabase (open-source Firebase alternative) with generous free tier and PostgreSQL database.

### Architecture Diagram

```
┌─────────────────────────────────────────┐
│         Android Application             │
├─────────────────────────────────────────┤
│  UI Layer (Jetpack Compose)             │
├─────────────────────────────────────────┤
│  AI Decision Engine (Kotlin)            │
│  - Active play decisions (real-time)    │
│  - Combat resolution                    │
│  - Interaction evaluation               │
├─────────────────────────────────────────┤
│  Game Logic Layer                       │
│  - Character management                 │
│  - d21 dice system                      │
│  - World rendering                      │
├─────────────────────────────────────────┤
│  Data Layer (Local Cache + Supabase)    │
│  - Room (offline cache)                 │
│  - Supabase client (sync)               │
└─────────────────────────────────────────┘
           │
           │ REST API / Realtime
           ▼
┌─────────────────────────────────────────┐
│         Supabase Backend                │
├─────────────────────────────────────────┤
│  PostgreSQL Database                    │
│  - characters (player data)             │
│  - activity_logs (history)              │
│  - locations (world state)              │
│  - encounters (social events)           │
│  - achievements                         │
├─────────────────────────────────────────┤
│  Edge Functions (Deno/TypeScript)       │
│  - offline-simulation                   │
│  - encounter-coordinator                │
│  - leaderboard-updater                  │
│  - achievement-checker                  │
├─────────────────────────────────────────┤
│  Realtime (WebSockets)                  │
│  - Live character positions             │
│  - Encounter notifications              │
├─────────────────────────────────────────┤
│  Storage (Optional)                     │
│  - Character sprites/avatars            │
│  - World assets                         │
└─────────────────────────────────────────┘
```

### Technology Stack

**Client (Android)**
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVI (Model-View-Intent) with Clean Architecture
- **Database:** Room (local cache)
- **Network:** Supabase Kotlin Client, Ktor
- **DI:** Hilt
- **Concurrency:** Kotlin Coroutines + Flow
- **Background:** WorkManager + Foreground Service (for active play)

**Backend (Supabase)**
- **Platform:** Supabase (Free tier: 500 MB database, 2 GB bandwidth, 50 MB file storage)
- **Database:** PostgreSQL with Row Level Security (RLS)
- **Functions:** Edge Functions (Deno runtime, 500K invocations/month free)
- **Realtime:** WebSocket subscriptions for live updates
- **Auth:** Supabase Auth (anonymous or email-based)

**Edge Functions (Optional: Cloudflare Workers as alternative)**
- For higher free tier limits (100K requests/day)
- Can handle offline simulation if Supabase limits hit

### Database Schema

```sql
-- Characters table
CREATE TABLE characters (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES auth.users(id),
    name VARCHAR(50) NOT NULL,
    level INTEGER DEFAULT 1,
    experience BIGINT DEFAULT 0,

    -- Stats
    strength INTEGER DEFAULT 1,
    intelligence INTEGER DEFAULT 1,
    agility INTEGER DEFAULT 1,
    luck INTEGER DEFAULT 1,
    charisma INTEGER DEFAULT 1,
    vitality INTEGER DEFAULT 1,

    -- State
    current_hp INTEGER,
    max_hp INTEGER,
    current_location VARCHAR(100),

    -- Personality (hidden from player initially)
    personality_courage FLOAT,
    personality_greed FLOAT,
    personality_curiosity FLOAT,
    personality_aggression FLOAT,
    personality_social FLOAT,
    personality_impulsive FLOAT,

    -- Meta
    job_class VARCHAR(50),
    gold BIGINT DEFAULT 0,
    last_active_at TIMESTAMPTZ DEFAULT NOW(),
    created_at TIMESTAMPTZ DEFAULT NOW(),

    -- JSON fields for flexibility
    inventory JSONB DEFAULT '[]',
    equipped_items JSONB DEFAULT '{}',
    discovered_locations JSONB DEFAULT '[]',
    active_quests JSONB DEFAULT '[]'
);

-- Activity logs table
CREATE TABLE activity_logs (
    id BIGSERIAL PRIMARY KEY,
    character_id UUID REFERENCES characters(id) ON DELETE CASCADE,
    timestamp TIMESTAMPTZ DEFAULT NOW(),
    activity_type VARCHAR(50), -- combat, exploration, social, quest, etc.
    description TEXT,
    rewards JSONB, -- {xp: 50, gold: 20, items: [...]}
    metadata JSONB, -- additional context
    is_major_event BOOLEAN DEFAULT FALSE,

    INDEX idx_character_timestamp (character_id, timestamp DESC)
);

-- Encounters table (for social features)
CREATE TABLE encounters (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    character1_id UUID REFERENCES characters(id),
    character2_id UUID REFERENCES characters(id),
    location VARCHAR(100),
    encounter_type VARCHAR(50), -- greeting, trade, party, combat, ignore
    status VARCHAR(50), -- initiated, negotiating, completed, failed
    outcome JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    completed_at TIMESTAMPTZ,

    INDEX idx_active_encounters (status, created_at)
);

-- Active locations table (for proximity matching)
CREATE TABLE active_locations (
    character_id UUID PRIMARY KEY REFERENCES characters(id) ON DELETE CASCADE,
    location VARCHAR(100),
    last_update TIMESTAMPTZ DEFAULT NOW(),
    is_available_for_encounters BOOLEAN DEFAULT TRUE,

    INDEX idx_location_lookup (location, last_update)
);

-- Achievements table
CREATE TABLE achievements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    character_id UUID REFERENCES characters(id) ON DELETE CASCADE,
    achievement_key VARCHAR(100),
    unlocked_at TIMESTAMPTZ DEFAULT NOW(),
    progress JSONB, -- for multi-step achievements

    UNIQUE(character_id, achievement_key)
);

-- Leaderboards (materialized view, refreshed periodically)
CREATE MATERIALIZED VIEW leaderboard_levels AS
SELECT id, name, level, job_class, experience
FROM characters
ORDER BY level DESC, experience DESC
LIMIT 100;

CREATE MATERIALIZED VIEW leaderboard_wealth AS
SELECT id, name, level, job_class, gold
FROM characters
ORDER BY gold DESC
LIMIT 100;
```

### AI Implementation

**Client-Side AI (Active Play):**

```kotlin
class HybridAI(
    private val character: Character,
    private val localWorldState: WorldState,
    private val repository: GameRepository
) {
    // Real-time decisions while player watches
    suspend fun makeRealTimeDecision(): Decision {
        val context = buildDecisionContext()

        return when (getPriority(context)) {
            Priority.SURVIVAL -> handleSurvival()
            Priority.CRITICAL_NEEDS -> handleNeeds()
            Priority.ACTIVE_QUEST -> continueQuest()
            Priority.OPPORTUNITY -> evaluateOpportunity()
            Priority.RESOURCE_GATHERING -> gatherResources()
            Priority.EXPLORATION -> explore()
            Priority.SOCIAL -> checkForEncounters() // Server query
            Priority.IDLE -> rest()
        }
    }

    private suspend fun checkForEncounters(): Decision {
        // Query Supabase for nearby characters
        val nearbyCharacters = repository.getNearbyCharacters(
            location = character.currentLocation,
            maxDistance = 100 // meters or location-based
        )

        if (nearbyCharacters.isEmpty()) return explore()

        // Evaluate each nearby character
        val target = nearbyCharacters.maxByOrNull { char ->
            evaluateSocialCompatibility(char)
        } ?: return explore()

        // Initiate encounter through server
        return initiateEncounter(target)
    }
}
```

**Server-Side AI (Offline Simulation):**

Edge Function: `offline-simulation`

```typescript
// supabase/functions/offline-simulation/index.ts
import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from 'https://esm.sh/@supabase/supabase-js@2'

interface SimulationRequest {
  characterId: string
  lastActiveAt: string
}

interface Decision {
  type: string
  target?: any
  location?: string
  outcome?: any
}

class ServerSideAI {
  character: any
  worldState: any

  constructor(character: any) {
    this.character = character
  }

  makeDecision(gameHour: number): Decision {
    // Simplified decision tree for offline play
    const healthPercent = this.character.current_hp / this.character.max_hp

    if (healthPercent < 0.3) {
      return { type: 'rest', outcome: this.rest() }
    }

    if (this.character.gold < 100) {
      return { type: 'hunt', outcome: this.huntMonsters() }
    }

    if (Math.random() < this.character.personality_curiosity) {
      return { type: 'explore', outcome: this.explore() }
    }

    return { type: 'quest', outcome: this.doQuest() }
  }

  huntMonsters(): any {
    // Probability-based combat
    const enemyLevel = this.character.level + Math.floor(Math.random() * 5 - 2)
    const winChance = this.calculateWinChance(enemyLevel)

    if (Math.random() < winChance) {
      const xp = enemyLevel * 10
      const gold = enemyLevel * 5
      return {
        success: true,
        xp,
        gold,
        description: `Defeated Level ${enemyLevel} monster`
      }
    } else {
      const damage = Math.floor(this.character.max_hp * 0.3)
      return {
        success: false,
        damage,
        description: `Failed to defeat Level ${enemyLevel} monster, took ${damage} damage`
      }
    }
  }

  rest(): any {
    const healed = Math.floor(this.character.max_hp * 0.5)
    return {
      healed,
      description: 'Rested at inn and recovered health'
    }
  }

  explore(): any {
    // Chance to discover new location
    if (Math.random() < 0.3) {
      return {
        discovered: this.getRandomLocation(),
        xp: 50,
        description: 'Discovered new location'
      }
    }
    return {
      description: 'Explored the area, found nothing of note'
    }
  }

  doQuest(): any {
    // Simplified quest completion
    const questDifficulty = this.character.level
    const successChance = 0.7

    if (Math.random() < successChance) {
      return {
        success: true,
        xp: questDifficulty * 20,
        gold: questDifficulty * 10,
        description: 'Completed quest'
      }
    }
    return {
      success: false,
      description: 'Quest failed'
    }
  }

  calculateWinChance(enemyLevel: number): number {
    const levelDiff = this.character.level - enemyLevel
    const baseDice = this.d21()
    const statModifier = (this.character.strength + this.character.agility) / 2

    const threshold = 15 - levelDiff - statModifier - baseDice
    return threshold <= 0 ? 0.8 : 0.3
  }

  d21(): number {
    return Math.floor(Math.random() * 21) + 1
  }

  getRandomLocation(): string {
    const locations = ['Dark Cave', 'Ancient Ruins', 'Hidden Grove', 'Bandit Camp']
    return locations[Math.floor(Math.random() * locations.length)]
  }
}

serve(async (req) => {
  try {
    const { characterId, lastActiveAt }: SimulationRequest = await req.json()

    // Initialize Supabase client
    const supabase = createClient(
      Deno.env.get('SUPABASE_URL') ?? '',
      Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''
    )

    // Fetch character data
    const { data: character, error: charError } = await supabase
      .from('characters')
      .select('*')
      .eq('id', characterId)
      .single()

    if (charError || !character) {
      throw new Error('Character not found')
    }

    // Calculate offline time
    const lastActive = new Date(lastActiveAt)
    const now = new Date()
    const offlineMinutes = (now.getTime() - lastActive.getTime()) / (1000 * 60)
    const gameHours = Math.floor(offlineMinutes / 10) // 1 real hour = 6 game hours

    // Limit simulation to prevent abuse
    const maxGameHours = 144 // 24 real hours = 144 game hours
    const simulationHours = Math.min(gameHours, maxGameHours)

    // Run simulation
    const ai = new ServerSideAI(character)
    const activities = []
    let currentState = { ...character }

    for (let hour = 0; hour < simulationHours; hour++) {
      const decision = ai.makeDecision(hour)

      // Apply outcome to character state
      if (decision.outcome) {
        if (decision.outcome.xp) {
          currentState.experience += decision.outcome.xp
          // Check for level up
          if (currentState.experience >= currentState.level * 100) {
            currentState.level++
            currentState.experience = 0
            // Auto-allocate stat points
            const stats = allocateStatsAutonomously(currentState)
            Object.assign(currentState, stats)

            activities.push({
              character_id: characterId,
              timestamp: new Date(lastActive.getTime() + hour * 60 * 60 * 1000),
              activity_type: 'level_up',
              description: `Reached Level ${currentState.level}!`,
              is_major_event: true,
              metadata: { stats }
            })
          }
        }
        if (decision.outcome.gold) {
          currentState.gold += decision.outcome.gold
        }
        if (decision.outcome.damage) {
          currentState.current_hp = Math.max(0, currentState.current_hp - decision.outcome.damage)
          if (currentState.current_hp === 0) {
            // Death
            currentState.current_hp = currentState.max_hp
            currentState.gold = Math.floor(currentState.gold * 0.9)
            activities.push({
              character_id: characterId,
              timestamp: new Date(lastActive.getTime() + hour * 60 * 60 * 1000),
              activity_type: 'death',
              description: 'Defeated in combat. Respawned in town.',
              is_major_event: true,
              metadata: { gold_lost: Math.floor(currentState.gold * 0.1) }
            })
          }
        }
        if (decision.outcome.healed) {
          currentState.current_hp = Math.min(currentState.max_hp, currentState.current_hp + decision.outcome.healed)
        }
      }

      // Log activity
      activities.push({
        character_id: characterId,
        timestamp: new Date(lastActive.getTime() + hour * 60 * 60 * 1000),
        activity_type: decision.type,
        description: decision.outcome?.description || `Performed ${decision.type}`,
        rewards: {
          xp: decision.outcome?.xp || 0,
          gold: decision.outcome?.gold || 0
        },
        is_major_event: decision.outcome?.success === true || decision.type === 'death'
      })
    }

    // Update character in database
    const { error: updateError } = await supabase
      .from('characters')
      .update({
        level: currentState.level,
        experience: currentState.experience,
        current_hp: currentState.current_hp,
        gold: currentState.gold,
        strength: currentState.strength,
        intelligence: currentState.intelligence,
        agility: currentState.agility,
        luck: currentState.luck,
        charisma: currentState.charisma,
        vitality: currentState.vitality,
        last_active_at: now
      })
      .eq('id', characterId)

    if (updateError) {
      throw updateError
    }

    // Insert activity logs (batch insert)
    const { error: logError } = await supabase
      .from('activity_logs')
      .insert(activities)

    if (logError) {
      throw logError
    }

    return new Response(
      JSON.stringify({
        success: true,
        simulatedHours: simulationHours,
        activitiesCount: activities.length,
        majorEvents: activities.filter(a => a.is_major_event),
        finalState: {
          level: currentState.level,
          hp: currentState.current_hp,
          gold: currentState.gold
        }
      }),
      { headers: { 'Content-Type': 'application/json' } }
    )

  } catch (error) {
    return new Response(
      JSON.stringify({ error: error.message }),
      { status: 500, headers: { 'Content-Type': 'application/json' } }
    )
  }
})

function allocateStatsAutonomously(character: any): any {
  // Job-class-based stat allocation
  const points = 3
  const jobPreferences = getJobStatPreferences(character.job_class)

  // Allocate based on preferences with some randomness
  const allocation = {
    strength: 0,
    intelligence: 0,
    agility: 0,
    luck: 0,
    charisma: 0,
    vitality: 0
  }

  for (let i = 0; i < points; i++) {
    const rand = Math.random()
    if (rand < jobPreferences.primary) {
      allocation[jobPreferences.primaryStat]++
    } else if (rand < jobPreferences.primary + jobPreferences.secondary) {
      allocation[jobPreferences.secondaryStat]++
    } else {
      // Random allocation
      const stats = ['strength', 'intelligence', 'agility', 'luck', 'charisma', 'vitality']
      const randomStat = stats[Math.floor(Math.random() * stats.length)]
      allocation[randomStat]++
    }
  }

  return {
    strength: character.strength + allocation.strength,
    intelligence: character.intelligence + allocation.intelligence,
    agility: character.agility + allocation.agility,
    luck: character.luck + allocation.luck,
    charisma: character.charisma + allocation.charisma,
    vitality: character.vitality + allocation.vitality
  }
}

function getJobStatPreferences(jobClass: string): any {
  const preferences = {
    'Warrior': { primaryStat: 'strength', secondaryStat: 'vitality', primary: 0.5, secondary: 0.3 },
    'Mage': { primaryStat: 'intelligence', secondaryStat: 'vitality', primary: 0.5, secondary: 0.3 },
    'Archer': { primaryStat: 'agility', secondaryStat: 'strength', primary: 0.5, secondary: 0.3 },
    'Rogue': { primaryStat: 'agility', secondaryStat: 'luck', primary: 0.4, secondary: 0.4 },
    'Priest': { primaryStat: 'intelligence', secondaryStat: 'charisma', primary: 0.5, secondary: 0.3 },
    // ... add others
  }
  return preferences[jobClass] || { primaryStat: 'vitality', secondaryStat: 'strength', primary: 0.5, secondary: 0.3 }
}
```

### Social Features Implementation

**Encounter Coordination Edge Function:**

```typescript
// supabase/functions/coordinate-encounter/index.ts
serve(async (req) => {
  const { character1Id, character2Id, location } = await req.json()

  const supabase = createClient(/*...*/)

  // Fetch both characters
  const { data: chars } = await supabase
    .from('characters')
    .select('*')
    .in('id', [character1Id, character2Id])

  if (chars.length !== 2) {
    return new Response(JSON.stringify({ error: 'Characters not found' }), { status: 404 })
  }

  const [char1, char2] = chars

  // Evaluate interaction type using both AIs
  const ai1 = new ServerSideAI(char1)
  const ai2 = new ServerSideAI(char2)

  const interaction1 = ai1.evaluateEncounter(char2)
  const interaction2 = ai2.evaluateEncounter(char1)

  // Negotiate interaction type (both must agree)
  const agreedInteraction = negotiateInteraction(interaction1, interaction2)

  // Create encounter record
  const { data: encounter } = await supabase
    .from('encounters')
    .insert({
      character1_id: character1Id,
      character2_id: character2Id,
      location,
      encounter_type: agreedInteraction.type,
      status: 'initiated'
    })
    .select()
    .single()

  // Execute encounter based on type
  let outcome = {}

  switch (agreedInteraction.type) {
    case 'trade':
      outcome = executeTrade(char1, char2)
      break
    case 'party':
      outcome = formParty(char1, char2)
      break
    case 'combat':
      outcome = resolveCombat(char1, char2)
      break
    case 'greeting':
      outcome = exchangeGreeting(char1, char2)
      break
    case 'ignore':
      outcome = { description: 'Characters passed by each other' }
      break
  }

  // Update encounter with outcome
  await supabase
    .from('encounters')
    .update({
      status: 'completed',
      outcome,
      completed_at: new Date()
    })
    .eq('id', encounter.id)

  // Log activities for both characters
  await supabase.from('activity_logs').insert([
    {
      character_id: character1Id,
      activity_type: 'social',
      description: `Encountered ${char2.name}. ${outcome.description}`,
      metadata: { encounter_id: encounter.id, encounter_type: agreedInteraction.type }
    },
    {
      character_id: character2Id,
      activity_type: 'social',
      description: `Encountered ${char1.name}. ${outcome.description}`,
      metadata: { encounter_id: encounter.id, encounter_type: agreedInteraction.type }
    }
  ])

  return new Response(JSON.stringify({ success: true, encounter, outcome }))
})
```

### Realtime Features

Use Supabase Realtime for live updates:

```kotlin
// Android client subscribing to encounter notifications
class EncounterService(private val supabase: SupabaseClient) {

    fun subscribeToEncounters(characterId: String) = flow {
        supabase.channel("encounters")
            .on<Encounter>(
                event = SupabaseEvent.INSERT,
                filter = {
                    or {
                        eq("character1_id", characterId)
                        eq("character2_id", characterId)
                    }
                }
            ) { encounter ->
                emit(encounter)
            }
            .subscribe()
    }
}
```

### Advantages

1. **True Offline Simulation:** Server processes while player is offline
2. **Robust Social Features:** Centralized coordination of encounters
3. **Scalability:** PostgreSQL handles growth well
4. **Free Tier:** Generous limits for MVP (500 MB DB, 2 GB bandwidth)
5. **Real-time Updates:** WebSocket support for live features
6. **Data Security:** Row Level Security (RLS) built-in
7. **Open Source:** Can self-host if needed
8. **Developer Experience:** Good tooling, SDKs for Kotlin/Android

### Disadvantages

1. **Server Dependency:** Offline simulation requires backend
2. **Complexity:** More moving parts than Option 1
3. **Latency:** Network calls for social features
4. **Cost at Scale:** Need to upgrade beyond free tier eventually

### Cost Estimate

**MVP (100-1K users):**
- Supabase Free Tier: **$0/month**
  - 500 MB database (enough for ~5K-10K characters)
  - 2 GB bandwidth
  - 500K Edge Function invocations
  - 2M Realtime messages
- Total: **$0/month**

**Growth (10K users):**
- Supabase Pro Plan: **$25/month**
  - 8 GB database (enough for 100K+ characters)
  - 50 GB bandwidth
  - 2M Edge Function invocations
  - 5M Realtime messages
- Additional costs (if exceeded):
  - Database: $0.125/GB beyond 8 GB
  - Bandwidth: $0.09/GB beyond 50 GB
- Estimated total: **$25-40/month**

**Scale (100K users):**
- Supabase Pro + overages: **~$100-200/month**
- Consider self-hosting at this scale

---

## Option 3: Cloud-Native with AI Services

### Overview

Leverage cloud AI services (OpenAI, Anthropic Claude, or Google Gemini) for truly dynamic, LLM-powered character autonomy. Server-heavy architecture with minimal client logic.

### Architecture Diagram

```
┌─────────────────────────────────────────┐
│         Android Application             │
├─────────────────────────────────────────┤
│  UI Layer (Jetpack Compose)             │
├─────────────────────────────────────────┤
│  Presentation Layer (Thin)              │
│  - Render game state                    │
│  - Display activity logs                │
│  - Handle user input (character create) │
├─────────────────────────────────────────┤
│  Data Layer (Remote-first)              │
│  - Room (minimal offline cache)         │
│  - API client (REST/GraphQL)            │
└─────────────────────────────────────────┘
           │
           │ HTTPS API
           ▼
┌─────────────────────────────────────────┐
│      Backend (Node.js/Python)           │
├─────────────────────────────────────────┤
│  API Layer (Express/FastAPI)            │
│  - Character CRUD                       │
│  - Game simulation endpoints            │
│  - WebSocket server (realtime)          │
├─────────────────────────────────────────┤
│  AI Decision Engine                     │
│  - LLM Integration (OpenAI/Claude)      │
│  - Prompt engineering for decisions     │
│  - Context management                   │
├─────────────────────────────────────────┤
│  Game Logic                             │
│  - World simulation                     │
│  - Combat resolution                    │
│  - Quest generation                     │
├─────────────────────────────────────────┤
│  Background Workers                     │
│  - Offline simulation queue (Bull)      │
│  - Encounter processing                 │
│  - Leaderboard updates                  │
└─────────────────────────────────────────┘
           │
           ▼
┌─────────────────────────────────────────┐
│         External Services               │
├─────────────────────────────────────────┤
│  LLM API (OpenAI / Anthropic / Gemini)  │
│  - Character decision-making            │
│  - Dynamic dialogue generation          │
│  - Quest creation                       │
├─────────────────────────────────────────┤
│  Database (PostgreSQL/MongoDB)          │
│  - Character data                       │
│  - Activity logs                        │
│  - World state                          │
├─────────────────────────────────────────┤
│  Cache (Redis)                          │
│  - Session data                         │
│  - Active character states              │
│  - Leaderboards                         │
├─────────────────────────────────────────┤
│  Message Queue (Redis/RabbitMQ)         │
│  - Offline simulation jobs              │
│  - Encounter processing                 │
└─────────────────────────────────────────┘
```

### Technology Stack

**Client (Android)**
- Minimal - just UI rendering and API calls

**Backend**
- **Runtime:** Node.js (TypeScript) or Python (FastAPI)
- **Framework:** NestJS (Node) or FastAPI (Python)
- **Database:** PostgreSQL (hosted: Supabase, Neon, or Railway)
- **Cache:** Redis (Upstash free tier)
- **Queue:** BullMQ (Redis-based)
- **Hosting:** Railway ($5/month), Render (free tier), or Fly.io

**AI Services**
- **Primary:** OpenAI GPT-4o-mini (cheapest, fast)
- **Alternative:** Google Gemini 1.5 Flash (free tier: 15 RPM, 1M TPM, 1500 RPD)
- **Alternative:** Anthropic Claude Haiku (budget option)

### AI Implementation (LLM-Powered)

**Decision-Making with LLM:**

```typescript
// backend/src/services/ai-decision.service.ts
import Anthropic from "@anthropic-ai/sdk";

interface Character {
  id: string
  name: string
  level: number
  stats: {
    strength: number
    intelligence: number
    agility: number
    luck: number
    charisma: number
    vitality: number
  }
  personality: {
    courage: number
    greed: number
    curiosity: number
    aggression: number
    social: number
    impulsive: number
  }
  jobClass: string
  currentHp: number
  maxHp: number
  location: string
}

interface GameContext {
  nearbyEnemies: any[]
  nearbyCharacters: any[]
  availableQuests: any[]
  currentQuest?: any
  timeOfDay: string
  weather: string
}

export class LLMDecisionEngine {
  private anthropic: Anthropic

  constructor() {
    this.anthropic = new Anthropic({
      apiKey: process.env.ANTHROPIC_API_KEY
    })
  }

  async makeDecision(character: Character, context: GameContext): Promise<Decision> {
    const prompt = this.buildPrompt(character, context)

    const message = await this.anthropic.messages.create({
      model: "claude-haiku-20240307", // Cheapest option
      max_tokens: 300,
      temperature: 0.7, // Some randomness for variety
      messages: [{
        role: "user",
        content: prompt
      }],
      // Use JSON mode if available, otherwise parse from text
    })

    const response = message.content[0].text
    const decision = this.parseDecision(response)

    return decision
  }

  private buildPrompt(character: Character, context: GameContext): string {
    return `You are simulating an autonomous RPG character. Based on the character's stats, personality, and current situation, decide what action they should take next.

CHARACTER PROFILE:
Name: ${character.name}
Level: ${character.level}
Job Class: ${character.jobClass}
HP: ${character.currentHp}/${character.maxHp}

STATS:
- Strength: ${character.stats.strength}
- Intelligence: ${character.stats.intelligence}
- Agility: ${character.stats.agility}
- Luck: ${character.stats.luck}
- Charisma: ${character.stats.charisma}
- Vitality: ${character.stats.vitality}

PERSONALITY TRAITS (0-1 scale):
- Courage vs Caution: ${character.personality.courage} (higher = more brave)
- Greed vs Generosity: ${character.personality.greed} (higher = more greedy)
- Curiosity vs Focus: ${character.personality.curiosity} (higher = more curious)
- Aggression vs Pacifism: ${character.personality.aggression} (higher = more aggressive)
- Independence vs Social: ${character.personality.social} (higher = more social)
- Impulsive vs Methodical: ${character.personality.impulsive} (higher = more impulsive)

CURRENT SITUATION:
Location: ${character.location}
Time of Day: ${context.timeOfDay}
Weather: ${context.weather}
Nearby Enemies: ${JSON.stringify(context.nearbyEnemies)}
Nearby Characters: ${JSON.stringify(context.nearbyCharacters)}
Available Quests: ${JSON.stringify(context.availableQuests)}
Active Quest: ${context.currentQuest ? JSON.stringify(context.currentQuest) : 'None'}

PRIORITY SYSTEM:
1. SURVIVAL (HP < 30%): Flee, heal, seek safety
2. CRITICAL_NEEDS: If lacking resources
3. ACTIVE_QUEST: Continue current quest
4. OPPORTUNITY: Evaluate risks/rewards
5. RESOURCE_GATHERING: Hunt, loot
6. EXPLORATION: Discover new areas
7. SOCIAL: Interact with nearby characters
8. IDLE: Rest, practice

Based on the character's personality and situation, what should they do next?

Respond in JSON format:
{
  "decision": "combat" | "explore" | "rest" | "quest" | "social" | "flee" | "trade",
  "target": "string (if applicable)",
  "reasoning": "brief explanation of why this fits the character",
  "priority": "survival" | "critical_needs" | "active_quest" | "opportunity" | "resource_gathering" | "exploration" | "social" | "idle"
}

Remember: Stay in character! A brave, aggressive warrior will act differently than a cautious, curious scholar.`
  }

  private parseDecision(response: string): Decision {
    try {
      // Try to extract JSON from response
      const jsonMatch = response.match(/\{[\s\S]*\}/)
      if (jsonMatch) {
        return JSON.parse(jsonMatch[0])
      }
    } catch (e) {
      // Fallback to default
    }

    // Default safe decision if parsing fails
    return {
      decision: 'rest',
      reasoning: 'Parse error, defaulting to safe action',
      priority: 'idle'
    }
  }
}
```

**Cost Optimization Strategies:**

1. **Use Gemini 1.5 Flash Free Tier:**
   - 15 requests/minute
   - 1 million tokens/minute
   - 1,500 requests/day
   - For MVP with <1K active users, this is FREE

2. **Decision Caching:**
   ```typescript
   // Cache similar situations to reduce API calls
   const cacheKey = `decision_${character.jobClass}_${healthPercent}_${context.hash}`
   const cached = await redis.get(cacheKey)
   if (cached) return JSON.parse(cached)
   ```

3. **Batch Processing:**
   - Process multiple offline hours with single LLM call
   - "Simulate 6 hours of gameplay for this character..."

4. **Hybrid Approach:**
   - Use LLM for complex/novel situations
   - Use rule-based for repetitive actions (combat, simple exploration)

### Advantages

1. **Truly Dynamic AI:** Characters feel genuinely autonomous and unpredictable
2. **Natural Language:** Can generate unique dialogue, quest descriptions
3. **Emergent Gameplay:** LLM can create unexpected but plausible scenarios
4. **Less Code:** Don't need to hardcode every decision path
5. **Future-Proof:** Easy to add new features (just update prompts)

### Disadvantages

1. **Cost:** LLM API calls expensive at scale
2. **Latency:** 500ms-2s per decision (too slow for real-time)
3. **Unpredictability:** LLM might make nonsensical decisions
4. **Rate Limits:** Free tiers very limited
5. **Dependency:** Reliant on external API availability
6. **Consistency:** Same character might act differently in identical situations

### Cost Estimate

**MVP (100 users, free tier Gemini):**
- Gemini 1.5 Flash: **$0/month** (under free tier limits)
- Backend hosting (Render/Railway): **$0-5/month**
- Database (Supabase): **$0/month**
- Redis (Upstash): **$0/month**
- Total: **$0-5/month**

**Growth (10K users, paid LLM):**

Assuming:
- 10K active daily users
- Each character makes 10 decisions/day (active play)
- Offline simulation: 5K characters/day, 1 call per 6 hours
- Total: 100K decisions + 5K simulations = 105K LLM calls/day

**Option A: Google Gemini 1.5 Flash (paid tier):**
- Input: ~500 tokens/call, Output: ~100 tokens/call
- Cost: $0.075 per 1M input tokens, $0.30 per 1M output tokens
- Daily: 105K * 500 = 52.5M input tokens = $3.94
- Daily: 105K * 100 = 10.5M output tokens = $3.15
- Total: **~$7/day = $210/month**

**Option B: OpenAI GPT-4o-mini:**
- Input: $0.150 per 1M tokens, Output: $0.600 per 1M tokens
- Daily: 52.5M * $0.15/1M = $7.88
- Daily: 10.5M * $0.60/1M = $6.30
- Total: **~$14/day = $420/month**

**Option C: Anthropic Claude Haiku:**
- Input: $0.25 per 1M tokens, Output: $1.25 per 1M tokens
- Daily: 52.5M * $0.25/1M = $13.13
- Daily: 10.5M * $1.25/1M = $13.13
- Total: **~$26/day = $780/month**

**Backend infrastructure:** ~$50/month

**Total for 10K users: $260-830/month** (primarily LLM costs)

### Reducing Costs

1. **Aggressive Caching:** Reduce calls by 50-70%
2. **Hybrid AI:** Use LLM only for complex decisions, rule-based for simple ones
3. **Batch Offline Simulation:** 1 call for 24 hours instead of multiple
4. **Self-Hosted LLM:** Use open models (Llama 3, Mistral) on GPU server

---

## Comparison Matrix

| Criteria | Option 1: Local-First | Option 2: Hybrid (Supabase) | Option 3: Cloud AI |
|----------|----------------------|----------------------------|-------------------|
| **Cost (MVP)** | $0/month | $0/month | $0-5/month |
| **Cost (10K users)** | $20/month | $25-40/month | $260-830/month |
| **AI Quality** | Rule-based, predictable | Rule-based, good | LLM-powered, excellent |
| **Social Features** | Basic, limited | Robust, real-time | Excellent, dynamic |
| **Offline Simulation** | Device (on app open) | Server (true offline) | Server (true offline) |
| **Real-time Performance** | Excellent (<50ms) | Good (100-300ms) | Poor (500-2000ms) |
| **Scalability** | Limited by Firebase | Excellent | Good (cost scales) |
| **Development Complexity** | Low | Medium | Medium-High |
| **Offline Functionality** | Full | Partial (needs sync) | Minimal |
| **Cheating Risk** | High (client-side) | Low (server authoritative) | Very Low |
| **Maintenance** | Low | Medium | Medium-High |
| **Future Flexibility** | Limited | Good | Excellent |
| **Tech Stack Complexity** | Simple | Moderate | Complex |
| **Vendor Lock-in** | Firebase | Supabase (can self-host) | LLM provider |

### Scoring (1-10, 10 best)

| Criteria | Weight | Option 1 | Option 2 | Option 3 |
|----------|--------|----------|----------|----------|
| **Cost Efficiency** | 25% | 10 | 9 | 3 |
| **Feature Completeness** | 20% | 6 | 9 | 10 |
| **Scalability** | 15% | 6 | 9 | 7 |
| **Development Speed** | 15% | 9 | 7 | 5 |
| **User Experience** | 15% | 7 | 9 | 8 |
| **Maintainability** | 10% | 8 | 7 | 6 |

**Weighted Scores:**
- **Option 1:** 7.9/10
- **Option 2:** 8.4/10 ⭐
- **Option 3:** 6.5/10

---

## Recommended Architecture: Option 2 (Hybrid with Supabase)

### Why Option 2 is Best

1. **Best Balance:** Combines cost efficiency with robust features
2. **True Offline Simulation:** Server processes while player is away (critical for "idle game" appeal)
3. **Scalable Social Features:** Centralized coordination enables rich multiplayer
4. **Free MVP:** Supabase free tier supports full feature set for launch
5. **Future-Proof:** Can scale to 100K+ users with reasonable costs
6. **Open Source:** Supabase can be self-hosted if needed
7. **Developer Experience:** Great tooling, active community, good docs
8. **PostgreSQL:** Battle-tested, powerful database for complex queries

### When to Consider Alternatives

**Choose Option 1 if:**
- Budget is absolute constraint ($0 forever)
- Social features are low priority
- You want simplest possible architecture
- Offline simulation on device is acceptable

**Choose Option 3 if:**
- Budget is flexible ($500+/month)
- You want cutting-edge AI experiences
- Dynamic, emergent gameplay is top priority
- You're willing to optimize heavily (caching, hybrid AI)

---

## Recommended Architecture Detailed Design

### System Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                    Android Application                       │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │         UI Layer (Jetpack Compose)                 │    │
│  │  - Character Creation Screen                       │    │
│  │  - Game View (Live character actions)              │    │
│  │  - Activity Log                                    │    │
│  │  - Character Sheet                                 │    │
│  │  - Inventory, Achievements, Lore                   │    │
│  └────────────────────────────────────────────────────┘    │
│                        │                                     │
│  ┌────────────────────────────────────────────────────┐    │
│  │    Presentation Layer (ViewModels)                 │    │
│  │  - CharacterViewModel                              │    │
│  │  - GameViewModel                                   │    │
│  │  - ActivityLogViewModel                            │    │
│  └────────────────────────────────────────────────────┘    │
│                        │                                     │
│  ┌────────────────────────────────────────────────────┐    │
│  │         Domain Layer (Use Cases)                   │    │
│  │  - MakeDecisionUseCase (active play)               │    │
│  │  - SyncCharacterUseCase                            │    │
│  │  - ProcessEncounterUseCase                         │    │
│  └────────────────────────────────────────────────────┘    │
│                        │                                     │
│  ┌────────────────────────────────────────────────────┐    │
│  │         AI Engine (Client-Side)                    │    │
│  │  - DecisionTree                                    │    │
│  │  - PersonalityEvaluator                            │    │
│  │  - CombatResolver (d21 system)                     │    │
│  │  - ActionSimulator                                 │    │
│  └────────────────────────────────────────────────────┘    │
│                        │                                     │
│  ┌────────────────────────────────────────────────────┐    │
│  │         Data Layer                                 │    │
│  │  ┌──────────────┐         ┌──────────────┐        │    │
│  │  │ Room DB      │         │ Supabase SDK │        │    │
│  │  │ (Local Cache)│         │ (Remote)     │        │    │
│  │  └──────────────┘         └──────────────┘        │    │
│  │         CharacterRepository                        │    │
│  │         ActivityLogRepository                      │    │
│  │         WorldStateRepository                       │    │
│  └────────────────────────────────────────────────────┘    │
│                                                              │
└──────────────────────────────────────────────────────────────┘
                         │
                         │ HTTPS / WebSocket
                         ▼
┌──────────────────────────────────────────────────────────────┐
│                    Supabase Backend                          │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │         PostgreSQL Database                        │    │
│  │  Tables:                                           │    │
│  │  - characters (player data)                        │    │
│  │  - activity_logs (history, last 500)               │    │
│  │  - active_locations (proximity matching)           │    │
│  │  - encounters (social interactions)                │    │
│  │  - achievements (unlocked achievements)            │    │
│  │  - world_state (shared game world)                 │    │
│  │  - leaderboards (materialized views)               │    │
│  └────────────────────────────────────────────────────┘    │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │         Edge Functions (Deno/TypeScript)           │    │
│  │  - offline-simulation (process offline hours)      │    │
│  │  - coordinate-encounter (social interactions)      │    │
│  │  - update-leaderboards (periodic job)              │    │
│  │  - check-achievements (on activity insert)         │    │
│  │  - allocate-stats (on level-up)                    │    │
│  └────────────────────────────────────────────────────┘    │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │         Realtime (WebSocket)                       │    │
│  │  - Encounter notifications                         │    │
│  │  - Nearby character updates                        │    │
│  │  - World event broadcasts                          │    │
│  └────────────────────────────────────────────────────┘    │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │         Auth (Supabase Auth)                       │    │
│  │  - Anonymous authentication                        │    │
│  │  - Optional email/password                         │    │
│  └────────────────────────────────────────────────────┘    │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │         Storage (Optional)                         │    │
│  │  - Character sprites                               │    │
│  │  - Achievement icons                               │    │
│  └────────────────────────────────────────────────────┘    │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### Data Flow

**1. Character Creation Flow**

```
User → UI → ViewModel → Repository → Supabase
                                         │
                                         ├─ INSERT into characters
                                         ├─ Generate personality traits
                                         └─ Return character ID
```

**2. Active Play Flow (Real-time decisions)**

```
Timer (3-10s) → AI Engine → Make Decision → Execute Locally
                                                 │
                                                 ├─ Update local state
                                                 ├─ Render animation
                                                 └─ Sync to Supabase (async)
```

**3. Offline Simulation Flow**

```
App Closed → Player returns after 2 hours
          │
          └─ App Opens
             │
             ├─ Call offline-simulation Edge Function
             │  (characterId, lastActiveAt)
             │
             └─ Edge Function:
                ├─ Calculate game hours (12 hours)
                ├─ Run AI simulation loop
                ├─ Generate activities
                ├─ Update character stats
                ├─ INSERT activity logs
                └─ Return summary

App Displays "While you were away..." screen with major events
```

**4. Social Encounter Flow**

```
Character1 enters location → Update active_locations table
                                    │
                                    └─ Realtime triggers Character2
                                           │
                                           └─ Character2 AI evaluates
                                                  │
                                                  └─ Call coordinate-encounter
                                                     Edge Function
                                                        │
                                                        ├─ Fetch both characters
                                                        ├─ Both AIs negotiate
                                                        ├─ Execute interaction
                                                        ├─ Log to encounters table
                                                        └─ Notify both clients

Both apps display interaction outcome in activity log
```

---

## Implementation Roadmap

### Phase 1: Foundation (Weeks 1-3)

**Week 1: Project Setup & Core Systems**
- [ ] Android project structure (MVVM + Clean Architecture)
- [ ] Supabase project setup
- [ ] Database schema creation
- [ ] Authentication setup (anonymous auth)
- [ ] Character model and database entities
- [ ] d21 dice system implementation
- [ ] Basic UI shell (Compose screens)

**Week 2: Character Creation & Basic AI**
- [ ] Character creation UI
- [ ] Stat allocation logic (validation)
- [ ] Job class selection
- [ ] Personality trait generation (hidden)
- [ ] Basic decision tree (Priority 1-3: Survival, Needs, Quest)
- [ ] Simple world state (1-2 locations)

**Week 3: Real-time Gameplay**
- [ ] Game view screen (character visualization)
- [ ] AI decision loop (3-10s timer)
- [ ] Basic combat system (d21 resolution)
- [ ] Activity log UI
- [ ] Local state management
- [ ] Sync to Supabase (character updates)

**Deliverable:** MVP with single-player autonomous character that can fight, explore, and level up.

---

### Phase 2: Offline Simulation (Weeks 4-5)

**Week 4: Server-Side Simulation**
- [ ] Edge Function: offline-simulation
- [ ] Server-side AI (simplified decision tree)
- [ ] Probability-based combat
- [ ] Activity generation with timestamps
- [ ] Stat allocation on level-up
- [ ] Handle character death/respawn

**Week 5: Offline Integration**
- [ ] App lifecycle handling (onPause, onResume)
- [ ] Call offline-simulation on app open
- [ ] "While you were away" summary screen
- [ ] Major events highlighting
- [ ] Activity log pagination (last 500 entries)
- [ ] Performance testing (24+ hours offline)

**Deliverable:** Full offline simulation - character progresses while player is away.

---

### Phase 3: World & Content (Weeks 6-7)

**Week 6: World Building**
- [ ] 5 regions implementation (Heartlands, Thornwood, Ashenveil, Frostpeak, Stormcoast)
- [ ] Location discovery system
- [ ] Fog-of-war (discovered_locations)
- [ ] Monster database (levels 1-50)
- [ ] NPC database (quest givers, merchants)
- [ ] Basic quest system

**Week 7: Progression Systems**
- [ ] Loot system (item database, rarity tiers)
- [ ] Inventory management (autonomous)
- [ ] Equipment system (auto-equip better items)
- [ ] Achievement system
- [ ] Leaderboards (materialized views)
- [ ] Character stats screen

**Deliverable:** Full single-player game with world, quests, loot, achievements.

---

### Phase 4: Social/Multiplayer (Weeks 8-9)

**Week 8: Encounter System**
- [ ] active_locations table and updates
- [ ] Proximity matching logic
- [ ] Edge Function: coordinate-encounter
- [ ] Encounter negotiation (AI-to-AI)
- [ ] Interaction types (greeting, trade, party, combat, ignore)
- [ ] Encounter resolution

**Week 9: Realtime Features**
- [ ] Supabase Realtime integration
- [ ] Live character position updates
- [ ] Encounter notifications
- [ ] Public character profiles
- [ ] Encounter history
- [ ] Social achievements

**Deliverable:** Full multiplayer with autonomous character-to-character interactions.

---

### Phase 5: Polish & Launch (Weeks 10-12)

**Week 10: UI/UX Polish**
- [ ] Animations and visual effects
- [ ] Onboarding flow
- [ ] Tutorial hints
- [ ] Settings screen
- [ ] Accessibility improvements
- [ ] Performance optimization

**Week 11: Testing & Balancing**
- [ ] Unit tests (AI logic, d21 system)
- [ ] Integration tests (offline simulation)
- [ ] E2E tests (character lifecycle)
- [ ] Game balance tuning (XP curve, difficulty)
- [ ] Bug fixes

**Week 12: Soft Launch**
- [ ] Beta testing (100 users)
- [ ] Analytics integration
- [ ] Crash reporting (Firebase Crashlytics)
- [ ] Performance monitoring
- [ ] User feedback collection
- [ ] Final polish

**Deliverable:** Production-ready app ready for Google Play release.

---

## Cost Breakdown (Option 2)

### MVP Phase (Months 1-3, 100-1,000 users)

**Infrastructure:**
- Supabase Free Tier: **$0/month**
  - 500 MB database
  - 2 GB bandwidth
  - 500K Edge Function invocations
  - 2M Realtime messages

**Development:**
- Developer time (12 weeks): Variable (solo dev or team)

**Total: $0/month recurring**

### Growth Phase (Months 4-12, 1K-10K users)

**Infrastructure:**
- Supabase Pro: **$25/month**
  - 8 GB database
  - 50 GB bandwidth
  - 2M Edge Function invocations
  - 5M Realtime messages

**Estimated Usage:**
- 10K registered users
- ~1K daily active users
- Database size: ~500 MB (10K characters × 50 KB)
- Bandwidth: ~20 GB/month (well under limit)
- Edge Functions: ~500K invocations/month (under limit)

**Additional Costs:**
- Analytics (optional): $0-10/month
- Crash reporting (Firebase): $0/month (free tier)
- CDN for assets (optional): $0-5/month

**Total: $25-40/month**

### Scale Phase (Year 2, 10K-100K users)

**Infrastructure:**
- Supabase Pro + overages: **$100-200/month**
  - Database: 8 GB base + ~2 GB overages = $0.25/GB × 2 = $0.50
  - Bandwidth: 50 GB base + ~50 GB overages = $0.09/GB × 50 = $4.50
  - Edge Functions: Likely still under limit

**OR: Self-Hosted Supabase:**
- VPS (DigitalOcean/Hetzner): $40-80/month
- Full control, unlimited usage
- Requires DevOps expertise

**Total: $100-200/month (managed) or $40-80/month (self-hosted)**

### Per-User Economics

At 10K users:
- Cost: $25-40/month
- Per user: **$0.0025-0.004/month** ($0.03-0.05/year)

At 100K users:
- Cost: $100-200/month
- Per user: **$0.001-0.002/month** ($0.012-0.024/year)

Highly scalable, sustainable business model.

---

## Risk Analysis

### Technical Risks

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| **Supabase free tier limits exceeded** | Medium | Medium | Monitor usage closely, plan upgrade path |
| **AI decisions feel repetitive** | Medium | High | Add randomness, expand decision tree, A/B test parameters |
| **Offline simulation takes too long** | Low | Medium | Optimize Edge Functions, limit max offline hours |
| **Cheating via client modification** | Medium | Medium | Server-authoritative for critical actions, validate on backend |
| **Database performance degradation** | Low | High | Optimize queries, add indexes, use materialized views |
| **Realtime connections limit hit** | Low | Medium | Limit concurrent connections, use polling fallback |

### Business Risks

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| **Low user retention** | Medium | High | Focus on game balance, compelling progression, achievements |
| **Social features unused** | Medium | Medium | Promote in-game, add incentives, make encounters exciting |
| **Cost scales faster than revenue** | Low | High | Monetization strategy (ads, IAP, premium), optimize infrastructure |
| **Competitor launches similar game** | Low | Medium | Focus on unique features (d21 system, personality, lore) |

### Operational Risks

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| **Supabase downtime** | Low | High | Implement local caching, graceful degradation, status page |
| **Data loss** | Very Low | Critical | Regular backups (Supabase auto-backups), test restore process |
| **GDPR/Privacy issues** | Low | High | Clear privacy policy, data export/deletion, anonymous auth option |

---

## Future Enhancements (Post-MVP)

### AI Improvements
- **Hybrid LLM AI:** Use LLM for special moments (boss encounters, major decisions) while keeping rule-based for routine actions
- **Learning System:** Characters learn from successes/failures, adjust personality over time
- **Dynamic Quest Generation:** LLM-generated quests based on character history

### Social Features
- **Guilds:** Characters autonomously join and contribute to guilds
- **PvP Arenas:** Scheduled autonomous tournaments
- **Character Relationships:** Friendships, rivalries, romance(?)

### Content Expansion
- **Crafting System:** Characters craft items based on blueprints
- **Housing:** Characters buy and decorate homes
- **Pets/Companions:** Autonomous allies
- **Additional Regions:** Expand beyond initial 5 regions
- **Prestige System:** Rebirth with bonuses

### Monetization
- **Ads:** Rewarded video ads for boosts (e.g., "Speed up offline simulation")
- **IAP:** Cosmetic items, character slots, remove ads
- **Premium Subscription:** Extra character slots, faster progression, exclusive cosmetics

---

## Conclusion

**Recommended Architecture: Option 2 - Hybrid with Supabase**

This architecture provides the best balance of:
- ✅ Cost efficiency ($0 for MVP, <$50 for 10K users)
- ✅ Feature completeness (full offline simulation + robust social)
- ✅ Scalability (can grow to 100K+ users)
- ✅ Developer experience (great tooling, active community)
- ✅ Future flexibility (can add LLM later, self-host if needed)

**Next Steps:**
1. Set up Supabase project and Android app structure
2. Implement Phase 1 (Foundation) - Weeks 1-3
3. Validate core loop with internal testing
4. Proceed to Phase 2 (Offline Simulation)
5. Launch MVP beta after Phase 4 (Social)

**Success Criteria for MVP:**
- Character autonomously plays for 24+ hours while player is offline
- Players check in 2-3x daily to see progress
- Average session length: 3-5 minutes (observation)
- Retention: 40%+ Day 1, 20%+ Day 7
- Social encounters happen naturally, feel meaningful

This implementation plan is achievable for a solo developer or small team within 12 weeks, with minimal costs and maximum flexibility for future growth.

---

**Document End**
