# Role Non-Playing Game - Functional Design Document

**Version:** 1.0
**Date:** 2025-11-13
**Status:** Draft Proposal

---

## 1. Game Concept & Vision

### 1.1 Core Concept

**Role Non-Playing Game** is an autonomous RPG that inverts traditional game mechanics: instead of
controlling a character, players create and observe their character living an independent life. The
character makes all decisions autonomously, explores the world, fights monsters, completes quests,
levels up, and interacts with other characters entirely on their own.

Players become **observers and documentarians** of their character's journey rather than active
controllers.

### 1.2 Unique Selling Points

- **Zero player control**: Characters are completely autonomous entities with their own agency
- **Emergent storytelling**: Each character's journey is unique, driven by AI decisions and
  personality
- **Idle/incremental mechanics**: Progress continues even when the app is closed
- **Social autonomous interactions**: Characters can meet and interact with other players'
  characters without player intervention
- **Achievement-driven engagement**: Characters can accomplish achievements and players can see
  those. Players themselves cannot accomplish any achievement.
- **Story archaeology**: Players discover lore and narrative through their character's experiences

### 1.3 Target Audience

- **Idle game enthusiasts** who enjoy progress-focused gameplay
- **RPG fans** curious about emergent gameplay and AI-driven narratives
- **Simulation game players** who enjoy observation and management
- **Casual gamers** who want RPG progression without active time commitment
- **Experimental gamers** interested in unique game mechanics

### 1.4 Core Pillars

1. **Autonomy**: Character independence is absolute
2. **Observation**: Player engagement through watching and learning
3. **Emergence**: Unique stories generated through AI behavior
4. **Persistence**: World and character exist continuously
5. **Discovery**: Uncovering character personality and world lore

---

## 2. Character System

### 2.1 Character Creation

When starting the game, players design their character's initial template:

#### 2.1.1 Visual Customization

- **Name**: Player assigns character name
- **Appearance**: Basic visual customization (sprite/avatar selection)
- **Gender**: Cosmetic choice (affects dialogue and interactions)

#### 2.1.2 Attribute Point Allocation

Players receive **25 points** to distribute across six core attributes:

- **Power** (STR): Physical damage, melee combat, carrying capacity
- **Intelligence** (INT): Magic damage, mana pool, learning speed
- **Agility** (AGI): Attack speed, dodge chance, movement speed
- **Luck** (LUK): Critical hit chance, loot quality, random event outcomes
- **Charm** (CHA): NPC interactions, trading prices, social success
- **Vitality** (VIT): Health points, regeneration, survival

**Constraints:**

- Minimum 1 point in each attribute
- Maximum 10 points in any single attribute at creation
- Total must equal 25 points

#### 2.1.3 Job Class Selection

Players choose one starting job that influences character behavior and abilities:

**Combat Classes:**

- **Warrior**: Focuses on melee combat, high survivability, prefers direct confrontation
- **Assassin**: Stealthy approach, critical strikes, risk-taking behavior
- **Rogue**: Stealth and cunning, picks locks and disarms traps, strikes from shadows, independent and resourceful behavior, opportunistic and untrustworthy in interactions
- **Archer**: Ranged combat, tactical positioning, cautious engagement

**Magic Classes:**

- **Mage**: Offensive magic, area damage, prefers magical solutions
- **Priest/Healer**: Support magic, defensive behavior, helps others
- **Warlock**: Dark magic, high-risk/high-reward, unconventional choices

**Support Classes:**

- **Bard**: Buff-focused, social interactions, avoids direct combat
- **Merchant**: Trading focus, values gold and items, negotiation preference
- **Scholar**: Exploration and lore-seeking, knowledge over combat

**Hybrid Classes:**

- **Paladin**: Balance of combat and healing
- **Battle Mage**: Combines physical and magical combat
- **Ranger**: Survival skills, exploration, balanced approach

### 2.2 Personality Traits

Upon creation, characters are assigned **3 hidden personality traits** that influence
decision-making:

**Personality Dimensions:**

- **Courage ↔ Caution**: Willingness to take risks vs. playing it safe
- **Greed ↔ Generosity**: Hoarding resources vs. sharing with others
- **Curiosity ↔ Focus**: Exploring freely vs. completing objectives
- **Aggression ↔ Pacifism**: Combat preference vs. avoiding conflict
- **Independence ↔ Social**: Solo behavior vs. seeking company
- **Impulsive ↔ Methodical**: Quick decisions vs. careful planning

These traits are influenced by:

- Initial stat distribution (high Luck = more risk-taking)
- Job class (Bard = more social)
- Random variation for uniqueness

**Player Discovery**: Players gradually learn their character's personality through observation.

### 2.3 Character Progression

#### 2.3.1 Leveling System

- Characters start at **Level 1**
- Experience gained through: combat, exploration, quest completion, social interactions
- Level cap: **50** (can be expanded post-launch)
- Experience requirements scale exponentially

#### 2.3.2 Skill Point Allocation (Autonomous)

Upon leveling up, characters receive **3 skill points** to allocate across their attributes.

**Allocation Logic:**
The character decides where to spend points based on:

1. **Job class preferences** (Warriors prefer STR/VIT, Mages prefer INT)
2. **Recent experiences** (frequent deaths → more VIT, missed attacks → more AGI)
3. **Personality traits** (risk-takers invest in damage, cautious ones in defense)
4. **Current build balance** (avoiding over-specialization unless personality dictates)
5. **Random variation** (5-10% chance of unexpected choice)

**Build Diversity**: Same starting character can develop very differently across playthroughs.

---

## 3. Autonomous AI Behavior System

### 3.1 Decision-Making Framework

Characters operate on a **priority-based decision tree** evaluated continuously:

**Priority Hierarchy:**

1. **Survival** (Health < 30%): Flee, heal, seek safety
2. **Critical Needs** (Hunger, equipment durability): Find food/repairs
3. **Active Quest** (If committed to quest): Continue quest objective
4. **Opportunity** (Rare events, encounters): Evaluate based on personality
5. **Resource Gathering** (Low gold, inventory space): Loot, trade, farm
6. **Exploration** (No immediate goals): Wander, discover new areas
7. **Social** (Other characters nearby): Interact, trade, party up
8. **Idle Behavior** (No stimuli): Rest, practice skills, reminisce

### 3.2 Action Resolution System (The d21 Mechanic)

All character actions that have a chance of success or failure are resolved using a **21-sided die
roll** combined with relevant character stats.

#### 3.2.1 The Roll

When a character attempts any action with uncertain outcome:

1. Roll a 21-sided die (d21): possible results are 1-21
2. Apply the following special cases:
    - **Roll of 21 (Critical Success)**: Action automatically succeeds, often with bonus effects
    - **Roll of 1 (Critical Failure)**: Action automatically fails, often with negative
      consequences
    - **Rolls of 2-20**: Success/failure determined by character stats vs. action difficulty

#### 3.2.2 Calculating Success (Rolls 2-20)

For rolls between 2-20, the formula is:

**Success Threshold** = Base Difficulty - (Relevant Stat Bonuses) - (Dice Roll)

- If threshold ≤ 0: **Success**
- If threshold > 0: **Failure**

**Relevant Stats by Action Type:**

- **Combat (Attack)**: Power (STR) or Agility (AGI) vs. enemy defense
- **Combat (Dodge)**: Agility (AGI) vs. enemy attack
- **Social Interaction**: Charm (CHA) vs. NPC disposition
- **Trap Detection/Disarm**: Intelligence (INT) or Agility (AGI) vs. trap complexity
- **Luck Events**: Luck (LUK) as primary modifier
- **Survival**: Vitality (VIT) vs. environmental hazard

#### 3.2.3 Difficulty Tiers

Actions have difficulty ratings that set the base threshold:

- **Trivial**: Difficulty 5 (almost always succeeds unless roll 1)
- **Easy**: Difficulty 10
- **Moderate**: Difficulty 15
- **Hard**: Difficulty 20
- **Very Hard**: Difficulty 25
- **Nearly Impossible**: Difficulty 30+

**Example:**

```
Character attempts to disarm a trap (Hard difficulty = 20)
Character has INT: 12, AGI: 8 (uses higher stat: INT)
Rolls d21: Gets 14

Calculation: 20 (difficulty) - 12 (INT) - 14 (roll) = -6
Result: Success! The trap is disarmed.

If they had rolled 3 instead:
20 - 12 - 3 = 5 (positive number)
Result: Failure. Trap triggers.
```

#### 3.2.4 Critical Results

**Critical Success (Natural 21):**

- Combat: Maximum damage + additional effect (knockdown, disarm, stun)
- Social: NPC becomes friendly/impressed, better rewards
- Loot: Find rare/additional items
- Trap: Disarm and recover trap components
- Exploration: Discover hidden passages or secrets

**Critical Failure (Natural 1):**

- Combat: Miss and become vulnerable (enemy gets advantage)
- Social: Offend NPC, worse prices, quest refusal
- Loot: Trigger trap or alert enemies
- Trap: Take full damage and alert nearby enemies
- Exploration: Get lost or stumble into danger

#### 3.2.5 Luck Stat Influence

The Luck (LUK) stat provides a unique benefit:

- **For every 5 points in LUK**: +1% chance to reroll a result of 1
- **For every 10 points in LUK**: Critical success range expands (at 20 LUK: rolls of 20-21 are
  both critical successes)
- **High LUK** also improves loot quality rolls

This makes Luck builds viable and exciting, as they can turn bad situations into unexpected wins.

#### 3.2.6 Activity Log Examples

The activity log should reflect the drama of dice rolls:

```
"Attacked Goblin Warrior. *CRITICAL HIT!* Dealt 45 damage. Goblin stunned."
"Attempted to persuade Merchant. Rolled poorly. Merchant unimpressed. Prices remain high."
"Tried to dodge Cave Bear's attack. *CRITICAL FAILURE!* Took 28 damage and knocked prone."
"Searched treasure chest. Lucky find! Discovered Epic item: Champion's Greatsword"
"Attempted to disarm trap. Success! Trap disabled. Recovered trap components."
```

### 3.3 Combat Behavior

**Engagement Decision:**

- **Aggression score** = (Power + Agility) / (Enemy threat level) × personality modifier
- If score > threshold: Attack
- If score < threshold: Avoid or flee
- Mid-range: Cautious approach, assess, then decide

**Combat Strategy:**

- Job class determines ability usage priority
- Each attack/defense action uses the d21 roll system
- Health monitoring triggers retreat at personality-dependent thresholds
- Loot evaluation after victory determines if character collects items

### 3.4 Exploration Behavior

**Movement Patterns:**

- **Curious characters**: Prioritize unexplored areas, ignore optimal paths
- **Focused characters**: Move efficiently toward objectives
- **Random wander chance**: 5-15% based on personality

**Discovery Mechanics:**

- Characters discover: new locations, NPCs, quests, loot, lore items
- Discovery logged in activity history
- Some discoveries unlock new behavioral options

### 3.5 Quest Engagement

**Quest Acquisition:**

- Characters encounter quests through: NPCs, exploration, random events
- **Acceptance logic**: Job alignment + personality fit + reward value
- Not all quests are accepted

**Quest Execution:**

- Character commits to quest for duration unless:
    - Survival threatens
    - Better opportunity appears (low commitment personalities)
- Quest completion grants: XP, gold, items, reputation

### 3.6 Social Interaction (Multiplayer)

When encountering another player's character:

**Interaction Types:**

1. **Greeting**: Brief acknowledgment, exchange pleasantries
2. **Trading**: Exchange items if both find value
3. **Party Formation**: Temporary cooperation for shared goals (max 30 min)
4. **Combat**: If personalities clash or resources contested (rare)
5. **Ignore**: Pass by without interaction

**Interaction Logic:**

- Charm stat influences success chance (uses d21 roll for interactions)
- Personality compatibility affects interaction type
- Both characters' AIs negotiate autonomously

### 3.7 Time-Based Behavior

**Active Play (Player watching):**

- Real-time actions: combat, movement, interactions visible
- Decision frequency: Every 3-10 seconds
- Player sees animations and immediate outcomes

**Offline Play (App closed):**

- Time-compressed simulation: 1 real hour = 6 game hours
- Activities: Exploration, combat, quests, rest
- Detailed activity log generated for player review
- Major events (level ups, deaths, rare finds) highlighted

---

## 4. Game World Design

### 4.1 World Structure

**Open World Layout:**

- **Central Hub**: Starting town, safe zone, basic services
- **Radiating Zones**: Difficulty increases with distance from hub
- **Biome Variety**: Forests, mountains, caves, ruins, villages, castles
- **Discovery-Based**: Areas become visible after character visits
- **Interconnected**: Multiple paths between locations

### 4.2 Location Types

**Towns & Villages:**

- Safe zones (no combat)
- NPCs offering: quests, trading, services (inn, blacksmith, temple)
- Social hub for character interactions
- Healing and resupply opportunities

**Wilderness Zones:**

- Open exploration areas
- Random encounters: monsters, events, loot
- Points of interest: caves, camps, shrines
- Varying difficulty tiers (Level 1-10, 11-20, etc.)

**Dungeons & Instances:**

- Contained challenge areas
- Boss encounters at completion
- High-value loot and XP
- One-time or respawning

**Landmarks:**

- Story-significant locations
- Unlock lore fragments
- Unique encounters or events

### 4.3 World Population

**Monsters:**

- Tiered by level and difficulty
- Different behaviors: aggressive, passive, territorial
- Loot tables based on type

**NPCs (Non-Player Characters):**

- Quest givers
- Merchants (buy/sell items)
- Service providers (healing, repairs)
- Lore keepers (story information)

**Other Player Characters:**

- Appear in world as autonomous agents
- Can be encountered for social interactions
- Represent real players' characters from server

### 4.4 Dynamic Events

**Random Encounters:**

- Traveling merchant appears
- Ambush by bandits
- Rare monster spawn
- Found treasure chest
- NPC in distress

**World Events:**

- Seasonal festivals (increased social activity)
- Monster invasions (increased combat)
- Merchant caravans (trading opportunities)

---

## 5. Progression & Advancement

### 5.1 Experience System

**XP Sources:**

- **Combat**: Defeating enemies (scaled to level difference)
- **Quest Completion**: Flat XP + bonus for difficulty
- **Discovery**: Finding new locations, lore items
- **Social Success**: Positive interactions, successful trades
- **Survival**: Days survived, challenges overcome

**Level Curve:**

- Early levels (1-10): Fast progression, core mechanics
- Mid levels (11-30): Moderate pace, build development
- Late levels (31-50): Slow, requires significant achievement

### 5.2 Loot System

**Item Types:**

- **Weapons**: Increase damage output
- **Armor**: Reduce damage taken
- **Accessories**: Stat bonuses, special effects
- **Consumables**: Potions, food, scrolls
- **Quest Items**: Required for specific quests
- **Lore Items**: Books, relics (unlock story)

**Rarity Tiers:**

- Common (gray) → Uncommon (green) → Rare (blue) → Epic (purple) → Legendary (gold)

**Acquisition:**

- Enemy drops (combat)
- Treasure chests (exploration)
- Quest rewards
- Trading with NPCs or players
- Crafting (future expansion)

**Inventory Management (Autonomous):**

- Characters evaluate items by: stat value, gold value, job relevance
- Auto-discard low-value items when inventory full
- Equip better items automatically
- Sell excess items at merchants

### 5.3 Currency System

**Gold:**

- Primary currency for transactions
- Earned through: loot, quest rewards, selling items
- Spent on: equipment, consumables, services, repairs

**Character Spending Behavior:**

- Job class influences priorities (Warriors buy weapons, Mages buy mana potions)
- Personality affects spending (Greedy characters hoard, Generous ones spend freely)
- Necessity overrides preference (healing when low HP)

---

## 6. User Interface & Experience

### 6.1 Main Game Screen

**Live View Panel (70% of screen):**

- Real-time visual representation of character
- Current location/environment displayed
- Active action shown with animation
- Other characters nearby visible
- Simple 2D top-down or side-view perspective

**Status Panel (30% of screen):**

**Character Stats (Compact):**

- Name, Level, Job Class
- HP/MP bars with numbers
- Current XP progress bar
- Core attributes (STR, INT, AGI, LUK, CHA, VIT) with values

**Current Status:**

- Active action: "Exploring Forest" / "Fighting Goblin" / "Resting in Tavern"
- Location name
- Time indicator (real-time clock)

**Quick Info:**

- Gold count
- Inventory space (e.g., "12/20 items")

### 6.2 Activity Log

**Access**: Scrollable panel or separate tab

**Log Categories:**

- **Combat**: "Defeated Level 5 Goblin (+50 XP, +12 Gold, found Iron Sword)"
- **Exploration**: "Discovered Dark Cave entrance"
- **Social**: "Met player character 'Eldrin'. Traded items."
- **Quests**: "Accepted quest: 'Clear the Rats' from Innkeeper" / "Completed quest (+200 XP, +50
  Gold)"
- **Level Up**: "Reached Level 8! Allocated points: +2 STR, +1 VIT"
- **Loot**: "Found Rare healing potion in chest"
- **Events**: "Encountered traveling merchant. Purchased mana potion."
- **Deaths**: "Defeated by Cave Bear. Respawned in town. Lost 10% gold."

**Timestamp Display:**

- Real-time events: "5 seconds ago"
- Offline events: "12:35 PM (while offline)"

**Filtering Options:**

- All events / Combat only / Important only

### 6.3 Inventory Screen

**Item Display:**

- Grid or list view of all items
- Equipped items highlighted
- Item details: name, rarity, stats, description

**Sorting Options:**

- By type, rarity, acquisition date

**Read-Only**: Player can view but not manage inventory

### 6.4 Character Sheet

**Detailed Information:**

- Full attribute breakdown with modifiers
- Equipment slots showing equipped items
- Complete ability list (from job class)
- Statistics: Total kills, deaths, quests completed, locations discovered
- Play time: Active and offline hours

**Personality Insights (Unlockable):**

- After sufficient observation, hints appear:
    - "Your character seems cautious in combat."
    - "Prefers exploring to grinding."
    - "Generous with other characters."

### 6.5 Achievement Panel

**Categories:**

- Combat achievements: "Slay 100 enemies", "Defeat a boss"
- Exploration: "Discover 20 locations", "Visit every town"
- Social: "Trade with 10 characters", "Form a party"
- Progression: "Reach Level 10", "Obtain legendary item"
- Story: "Unlock 5 lore fragments", "Complete main quest"

**Rewards:**

- Title unlocks for character
- Cosmetic rewards
- Stat tracking and bragging rights

### 6.6 Story/Lore Panel

**Lore Fragments:**

- Character discovers books, relics, NPCs with stories
- Each discovery adds entry to lore codex
- Categories: World history, faction lore, character backstories
- Completion percentage tracked

---

## 7. Social & Multiplayer Systems

### 7.1 Character Encounters

**Server Architecture:**

- Characters exist on shared servers
- Proximity-based encounter system
- When two characters enter same location: potential interaction

### 7.2 Autonomous Interactions

**Encounter Flow:**

1. Characters detect each other
2. Both AIs evaluate: personality compatibility, needs, goals
3. Interaction type selected by both AIs (must agree)
4. Interaction executes (trade, party, ignore, compete)
5. Outcome logged for both players

**Trading:**

- AIs evaluate item value vs. need
- Haggling simulated through Charm checks
- Successful trades benefit both characters

**Party Formation:**

- Temporary cooperation (15-30 minutes)
- Shared quest completion or exploration
- XP and loot shared based on contribution
- Party dissolves after: time limit, goal completion, or conflict

### 7.3 Passive Social Features

**Character Profiles (Public):**

- Visible to other players
- Shows: Name, Level, Job, Appearance
- Achievement showcase (player-selected)
- Total stats: Quests completed, monsters defeated

**Encounter History:**

- Log of interactions with other player characters
- "Your character met 'Thorin' 3 times. Friendly relationship."

**Leaderboards:**

- Global rankings: Highest level, most achievements, richest character
- Optional participation

---

## 8. Achievement & Discovery Systems

### 8.1 Achievement Categories

**Progression Achievements:**

- Level milestones (10, 25, 50)
- Skill point totals
- Equipment rarity obtained

**Combat Achievements:**

- Enemy type defeats (100 goblins, 50 dragons)
- Boss defeats
- Survival streaks (days without death)
- Perfect victories (no damage taken)

**Exploration Achievements:**

- Location discoveries (all towns, all dungeons)
- Distance traveled
- Rare location finds

**Social Achievements:**

- Character encounters
- Successful trades
- Party completions
- Reputation levels with NPC factions

**Collection Achievements:**

- Lore fragments collected
- Legendary items obtained
- Complete equipment sets

**Story Achievements:**

- Quest completions (total count)
- Major quest lines finished
- Secret discoveries

### 8.2 Story Discovery

**Lore Collection:**

- World history revealed through fragments
- Character finds: ancient texts, artifacts, NPC stories
- Fragments connect to form larger narrative
- Some lore locked behind rare discoveries

**Narrative Threads:**

- Multiple story arcs in world
- Characters naturally uncover stories through play
- No forced narrative – discovery-based

**Personal Character Story:**

- Each character generates unique journey narrative
- Key moments saved: first kill, first death, rare finds
- Story summary available for review

---

## 9. Core Game Loop

### 9.1 Player Session Flow

**Opening App:**

1. Load character data
2. Display "While you were away" summary screen:
    - Level changes
    - Major events (deaths, rare loot, level ups)
    - Time elapsed
3. Show current character status
4. Resume real-time autonomous play

**During Session:**

1. Watch character actions in real-time
2. Check activity log for detailed information
3. Review inventory and character growth
4. Check achievements and lore discoveries
5. Observe character personality through actions

**Closing App:**

1. Character continues playing offline
2. Time-compressed simulation begins
3. Progress saved continuously

### 9.2 Short-Term Loop (Per Session)

**Player Engagement:**

- Watch 1-5 minutes of real-time action
- Check what happened offline
- Evaluate character growth and decisions
- Discover new achievements or lore

**Character Actions:**

- Complete 1-2 combat encounters
- Progress on current quest or exploration
- Potentially level up or find notable loot

### 9.3 Medium-Term Loop (Days/Weeks)

**Player Goals:**

- See character reach next level milestone (every 5 levels)
- Complete major quest lines character undertakes
- Unlock new areas through character exploration
- Achieve specific achievements

**Character Development:**

- Build becomes more defined
- Personality more evident through patterns
- Social interactions increase with other players

### 9.4 Long-Term Loop (Months)

**Player Investment:**

- Reach maximum level
- Complete lore codex
- Obtain legendary equipment
- Max out achievement completion

**Endgame Content:**

- High-level zones and challenges
- Rare boss encounters
- Legendary quest lines
- Create additional characters with different builds

---

## 10. Technical Considerations (High-Level)

### 10.1 Data Persistence

**Character Data:**

- Attributes, inventory, location, quest states
- Activity log (limited retention: last 500 entries)
- Achievement progress
- Offline simulation state

**World State:**

- Player progress in world (discovered locations)
- NPC interaction states
- Active quests and completions

**Save Frequency:**

- Real-time: Every action committed immediately
- Offline: Simulation saved periodically

### 10.2 Offline Simulation

**Time Compression:**

- 1 real hour = 6 game hours when offline
- Prevents excessive overnight progression
- Maintains engagement incentive for checking in

**Simulation Accuracy:**

- Simplified combat (probability-based outcomes)
- Movement and exploration (path simulation)
- Quest progress (objective tracking)
- No social interactions when offline (requires both players)

**Activity Generation:**

- Events logged with timestamps
- Important events flagged for summary
- Prevents log spam (consolidates similar events)

### 10.3 Performance & Scalability

**Client-Side Processing:**

- Real-time AI decisions run on device
- Animation and rendering
- UI updates

**Server-Side Processing:**

- Character state synchronization
- Multiplayer encounter matching
- Offline simulation execution
- Leaderboards and global data

**Optimization Needs:**

- Efficient AI decision trees
- Minimal network calls (batch updates)
- Caching for static world data

### 10.4 Balance & Tuning

**Difficulty Scaling:**

- Enemy levels scale with character level in zones
- Death penalties prevent infinite retries benefit
- Resource scarcity maintains tension

**Progression Pacing:**

- Level curve prevents too-fast or too-slow progression
- Offline progression capped to encourage active play
- Reward frequency maintains engagement

**Personality Balance:**

- All personality types viable
- No "optimal" personality – variety encouraged
- Edge cases handled (e.g., pacifist in combat zone)

---

## 11. Future Expansion Possibilities

While these are out of scope for initial launch, potential expansions include:

### 11.1 Additional Features

- **Crafting system**: Characters learn and craft items autonomously
- **Housing**: Characters acquire and decorate homes
- **Pets/Companions**: Autonomous allies that follow character
- **Prestige system**: Restart at Level 1 with bonuses
- **Seasonal events**: Limited-time content and rewards

### 11.2 Enhanced Social Systems

- **Guilds**: Characters autonomously join and contribute to guilds
- **PvP arenas**: Scheduled autonomous tournaments
- **Character relationships**: Friendships and rivalries develop

### 11.3 Narrative Expansion

- **Dynamic quests**: Procedurally generated story content
- **World events**: Server-wide story progressions
- **Character legacy**: Retired characters become NPCs

---

## 12. Success Metrics

### 12.1 Player Engagement

- Daily active users (DAU)
- Average session length
- Session frequency
- Retention rates (Day 1, 7, 30)

### 12.2 Character Progression

- Average player level
- Time to reach level milestones
- Character diversity (stat distributions and job classes)

### 12.3 Social Engagement

- Encounter frequency
- Successful interaction rate
- Trades completed
- Party formations

### 12.4 Content Completion

- Achievement completion rates
- Lore discovery rates
- Quest completion averages
- Area discovery rates

---

## 13. Design Philosophy Summary

**Core Values:**

1. **Respect Player Time**: Progress happens offline, no FOMO pressure
2. **Embrace Emergence**: Unique stories through AI autonomy
3. **Pure Observation**: Zero control maintains game integrity
4. **Discovery Over Direction**: Players learn by watching
5. **Social Autonomy**: Multiplayer without coordination burden

**Design Constraints:**

1. **No Player Control**: Character decisions are inviolable
2. **Observable Progress**: All actions must be visible/logged
3. **Meaningful Choices**: Initial character creation impacts journey
4. **Balanced Automation**: Progression engaging but not overwhelming
5. **Technical Feasibility**: AI complexity must be implementable on mobile

---

## 14. Conclusion

**Role Non-Playing Game** inverts the traditional RPG formula, creating a unique experience where
players are storytellers rather than heroes. By removing direct control, the game emphasizes
observation, discovery, and emergent narrative, tapping into the same engagement loops as idle games
while delivering the depth and variety of traditional RPGs.

The hybrid AI system (rule-based with personality quirks) ensures characters feel alive and
unpredictable while remaining technically feasible. The open world exploration creates endless
variety, while social features and achievements provide long-term engagement hooks.

This design document provides the functional blueprint for development. The next phase will involve
technical architecture design, followed by prototyping core systems (character creation, AI
decision-making, and UI).

---

**Document End**