# Supabase Mission System Database Schema

This document contains the SQL schema for implementing the mission system in Supabase.

## Overview

The mission system requires the following new tables:
1. `principal_mission_progress` - Track character progress on principal missions
2. `secondary_mission_progress` - Track character progress on secondary missions
3. `lore_discoveries` - Track what lore the character has discovered
4. `mission_step_progress` - Track individual step completion within principal missions

We also need to update the existing `characters` table to include mission-related fields.

---

## 1. Update Characters Table

```sql
-- Add mission-related columns to existing characters table
ALTER TABLE characters
ADD COLUMN IF NOT EXISTS active_principal_mission_id TEXT,
ADD COLUMN IF NOT EXISTS principal_mission_started_at TIMESTAMPTZ,
ADD COLUMN IF NOT EXISTS principal_mission_completed_count INTEGER DEFAULT 0;

-- Add comment explaining the new columns
COMMENT ON COLUMN characters.active_principal_mission_id IS 'ID of the currently active principal mission (e.g., pm_warrior_1)';
COMMENT ON COLUMN characters.principal_mission_started_at IS 'When the current principal mission was assigned';
COMMENT ON COLUMN characters.principal_mission_completed_count IS 'Total number of principal missions completed by this character';
```

---

## 2. Principal Mission Progress Table

```sql
-- Create table for tracking principal mission progress
CREATE TABLE IF NOT EXISTS principal_mission_progress (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    character_id UUID NOT NULL REFERENCES characters(id) ON DELETE CASCADE,
    mission_id TEXT NOT NULL, -- e.g., "pm_warrior_1"

    -- Progress tracking
    status TEXT NOT NULL DEFAULT 'in_progress', -- 'in_progress', 'completed', 'abandoned'
    completed_step_ids TEXT[] DEFAULT ARRAY[]::TEXT[], -- Array of completed step IDs
    boss_encountered BOOLEAN DEFAULT FALSE,
    boss_defeated BOOLEAN DEFAULT FALSE,

    -- Timestamps
    started_at TIMESTAMPTZ DEFAULT NOW(),
    completed_at TIMESTAMPTZ,
    last_progress_at TIMESTAMPTZ DEFAULT NOW(),

    -- Metadata
    progress_percentage INTEGER DEFAULT 0, -- Calculated: (steps_complete + boss) / total_steps

    UNIQUE(character_id, mission_id),

    CONSTRAINT valid_status CHECK (status IN ('in_progress', 'completed', 'abandoned')),
    CONSTRAINT valid_percentage CHECK (progress_percentage >= 0 AND progress_percentage <= 100)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_principal_mission_progress_character
    ON principal_mission_progress(character_id);
CREATE INDEX IF NOT EXISTS idx_principal_mission_progress_status
    ON principal_mission_progress(character_id, status);

-- Add comments
COMMENT ON TABLE principal_mission_progress IS 'Tracks character progress on principal story missions';
COMMENT ON COLUMN principal_mission_progress.mission_id IS 'References mission ID from PrincipalMissionsRepository (not FK as data is in code)';
COMMENT ON COLUMN principal_mission_progress.completed_step_ids IS 'Array of step IDs that have been discovered/completed';
```

---

## 3. Secondary Mission Progress Table

```sql
-- Create table for tracking secondary mission progress
CREATE TABLE IF NOT EXISTS secondary_mission_progress (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    character_id UUID NOT NULL REFERENCES characters(id) ON DELETE CASCADE,
    mission_id TEXT NOT NULL, -- e.g., "sm_001"

    -- Progress tracking
    status TEXT NOT NULL DEFAULT 'ongoing', -- 'ongoing', 'completed'
    current_progress TEXT, -- Descriptive text of current progress

    -- Timestamps
    discovered_at TIMESTAMPTZ DEFAULT NOW(),
    completed_at TIMESTAMPTZ,

    -- Rewards (stored when completed)
    reward_experience BIGINT,
    reward_gold INTEGER,
    reward_equipment TEXT, -- Name of equipment received, if any

    UNIQUE(character_id, mission_id),

    CONSTRAINT valid_secondary_status CHECK (status IN ('ongoing', 'completed'))
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_secondary_mission_progress_character
    ON secondary_mission_progress(character_id);
CREATE INDEX IF NOT EXISTS idx_secondary_mission_progress_status
    ON secondary_mission_progress(character_id, status);

-- Add comments
COMMENT ON TABLE secondary_mission_progress IS 'Tracks character progress on secondary side missions';
COMMENT ON COLUMN secondary_mission_progress.mission_id IS 'References mission ID from SecondaryMissionsRepository';
COMMENT ON COLUMN secondary_mission_progress.current_progress IS 'Human-readable description of progress for UI display';
```

---

## 4. Lore Discoveries Table

```sql
-- Create table for tracking discovered lore
CREATE TABLE IF NOT EXISTS lore_discoveries (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    character_id UUID NOT NULL REFERENCES characters(id) ON DELETE CASCADE,

    -- Lore content
    lore_category TEXT NOT NULL, -- 'WORLD_HISTORY', 'REGIONS', 'MYSTERIES', etc.
    lore_title TEXT NOT NULL,
    lore_content TEXT NOT NULL,

    -- Source of discovery
    source_type TEXT NOT NULL, -- 'PRINCIPAL_MISSION_STEP', 'BOSS_BATTLE', 'LOCATION_DISCOVERY', etc.
    source_id TEXT, -- Mission ID, boss ID, location ID, etc.

    -- Timestamp
    discovered_at TIMESTAMPTZ DEFAULT NOW(),

    -- Prevent duplicate discoveries
    UNIQUE(character_id, lore_title),

    CONSTRAINT valid_lore_category CHECK (lore_category IN (
        'WORLD_HISTORY', 'REGIONS', 'FACTIONS', 'MYSTERIES',
        'LEGENDARY_CREATURES', 'HERO_JOURNEY', 'ARTIFACTS'
    )),
    CONSTRAINT valid_source_type CHECK (source_type IN (
        'PRINCIPAL_MISSION_STEP', 'BOSS_BATTLE', 'LOCATION_DISCOVERY',
        'ITEM_ACQUIRED', 'NPC_INTERACTION', 'DEFAULT'
    ))
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_lore_discoveries_character
    ON lore_discoveries(character_id);
CREATE INDEX IF NOT EXISTS idx_lore_discoveries_category
    ON lore_discoveries(character_id, lore_category);
CREATE INDEX IF NOT EXISTS idx_lore_discoveries_discovered_at
    ON lore_discoveries(character_id, discovered_at DESC);

-- Add comments
COMMENT ON TABLE lore_discoveries IS 'Tracks lore fragments discovered by the character during their journey';
COMMENT ON COLUMN lore_discoveries.lore_category IS 'Category from LoreCategory enum';
COMMENT ON COLUMN lore_discoveries.source_type IS 'How the lore was discovered (from LoreSourceType enum)';
```

---

## 5. Row Level Security (RLS) Policies

```sql
-- Enable RLS on all mission tables
ALTER TABLE principal_mission_progress ENABLE ROW LEVEL SECURITY;
ALTER TABLE secondary_mission_progress ENABLE ROW LEVEL SECURITY;
ALTER TABLE lore_discoveries ENABLE ROW LEVEL SECURITY;

-- Principal Mission Progress Policies
CREATE POLICY "Users can view their own principal mission progress"
    ON principal_mission_progress FOR SELECT
    USING (character_id IN (
        SELECT id FROM characters WHERE user_id = auth.uid()
    ));

CREATE POLICY "Users can insert their own principal mission progress"
    ON principal_mission_progress FOR INSERT
    WITH CHECK (character_id IN (
        SELECT id FROM characters WHERE user_id = auth.uid()
    ));

CREATE POLICY "Users can update their own principal mission progress"
    ON principal_mission_progress FOR UPDATE
    USING (character_id IN (
        SELECT id FROM characters WHERE user_id = auth.uid()
    ));

-- Secondary Mission Progress Policies
CREATE POLICY "Users can view their own secondary mission progress"
    ON secondary_mission_progress FOR SELECT
    USING (character_id IN (
        SELECT id FROM characters WHERE user_id = auth.uid()
    ));

CREATE POLICY "Users can insert their own secondary mission progress"
    ON secondary_mission_progress FOR INSERT
    WITH CHECK (character_id IN (
        SELECT id FROM characters WHERE user_id = auth.uid()
    ));

CREATE POLICY "Users can update their own secondary mission progress"
    ON secondary_mission_progress FOR UPDATE
    USING (character_id IN (
        SELECT id FROM characters WHERE user_id = auth.uid()
    ));

-- Lore Discoveries Policies
CREATE POLICY "Users can view their own lore discoveries"
    ON lore_discoveries FOR SELECT
    USING (character_id IN (
        SELECT id FROM characters WHERE user_id = auth.uid()
    ));

CREATE POLICY "Users can insert their own lore discoveries"
    ON lore_discoveries FOR INSERT
    WITH CHECK (character_id IN (
        SELECT id FROM characters WHERE user_id = auth.uid()
    ));
```

---

## 6. Helper Functions

```sql
-- Function to calculate principal mission progress percentage
CREATE OR REPLACE FUNCTION calculate_mission_progress_percentage(
    p_mission_id TEXT,
    p_completed_steps TEXT[],
    p_boss_defeated BOOLEAN
) RETURNS INTEGER AS $$
DECLARE
    total_steps INTEGER;
    completed_count INTEGER;
BEGIN
    -- This is a simplified version - in production you'd query mission data
    -- For now, assume 4 steps + 1 boss = 5 total
    total_steps := 5;
    completed_count := array_length(p_completed_steps, 1);
    IF completed_count IS NULL THEN
        completed_count := 0;
    END IF;

    IF p_boss_defeated THEN
        completed_count := completed_count + 1;
    END IF;

    RETURN (completed_count * 100) / total_steps;
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Trigger to auto-update progress percentage
CREATE OR REPLACE FUNCTION update_mission_progress_percentage()
RETURNS TRIGGER AS $$
BEGIN
    NEW.progress_percentage := calculate_mission_progress_percentage(
        NEW.mission_id,
        NEW.completed_step_ids,
        NEW.boss_defeated
    );
    NEW.last_progress_at := NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_mission_progress
    BEFORE INSERT OR UPDATE ON principal_mission_progress
    FOR EACH ROW
    EXECUTE FUNCTION update_mission_progress_percentage();
```

---

## 7. Sample Queries

### Insert a new principal mission for a character
```sql
INSERT INTO principal_mission_progress (character_id, mission_id, status)
VALUES (
    'character-uuid-here',
    'pm_warrior_1',
    'in_progress'
);
```

### Discover a mission step
```sql
UPDATE principal_mission_progress
SET completed_step_ids = array_append(completed_step_ids, 'pm_warrior_1_step_1'),
    last_progress_at = NOW()
WHERE character_id = 'character-uuid-here'
  AND mission_id = 'pm_warrior_1';
```

### Check if boss can be encountered
```sql
SELECT
    mission_id,
    completed_step_ids,
    boss_encountered,
    boss_defeated,
    array_length(completed_step_ids, 1) as steps_complete
FROM principal_mission_progress
WHERE character_id = 'character-uuid-here'
  AND mission_id = 'pm_warrior_1'
  AND boss_defeated = FALSE
  AND array_length(completed_step_ids, 1) >= 4; -- All 4 steps complete
```

### Discover a secondary mission
```sql
INSERT INTO secondary_mission_progress (character_id, mission_id, status, current_progress)
VALUES (
    'character-uuid-here',
    'sm_001',
    'ongoing',
    'Visit all major locations in the Heartlands region'
)
ON CONFLICT (character_id, mission_id) DO NOTHING;
```

### Complete a secondary mission
```sql
UPDATE secondary_mission_progress
SET status = 'completed',
    completed_at = NOW(),
    reward_experience = 200,
    reward_gold = 100,
    reward_equipment = 'Rare Sword'
WHERE character_id = 'character-uuid-here'
  AND mission_id = 'sm_001';
```

### Add a lore discovery
```sql
INSERT INTO lore_discoveries (
    character_id,
    lore_category,
    lore_title,
    lore_content,
    source_type,
    source_id
)
VALUES (
    'character-uuid-here',
    'MYSTERIES',
    'The Calling',
    'Every adventurer who arrives on Aethermoor reports the same experience—a pull, a calling...',
    'PRINCIPAL_MISSION_STEP',
    'pm_warrior_1_step_1'
)
ON CONFLICT (character_id, lore_title) DO NOTHING;
```

### Get all discovered lore for a character
```sql
SELECT
    lore_category,
    lore_title,
    lore_content,
    discovered_at
FROM lore_discoveries
WHERE character_id = 'character-uuid-here'
ORDER BY discovered_at DESC;
```

### Get active missions summary
```sql
SELECT
    'Principal' as mission_type,
    pmp.mission_id,
    pmp.progress_percentage,
    pmp.last_progress_at
FROM principal_mission_progress pmp
WHERE pmp.character_id = 'character-uuid-here'
  AND pmp.status = 'in_progress'

UNION ALL

SELECT
    'Secondary' as mission_type,
    smp.mission_id,
    CASE WHEN smp.status = 'completed' THEN 100 ELSE 50 END as progress_percentage,
    COALESCE(smp.completed_at, smp.discovered_at) as last_progress_at
FROM secondary_mission_progress smp
WHERE smp.character_id = 'character-uuid-here'
  AND smp.status = 'ongoing'

ORDER BY last_progress_at DESC;
```

---

## 8. Migration Order

Apply these changes in the following order:

1. **Step 1**: Update characters table (add mission columns)
2. **Step 2**: Create principal_mission_progress table
3. **Step 3**: Create secondary_mission_progress table
4. **Step 4**: Create lore_discoveries table
5. **Step 5**: Enable RLS and create policies
6. **Step 6**: Create helper functions and triggers

---

## 9. Integration with Kotlin Code

### Kotlin Model Updates

After applying the schema, update your Kotlin models:

```kotlin
// Add to Character.kt
data class Character(
    // ... existing fields ...
    val activePrincipalMissionId: String? = null,
    val principalMissionStartedAt: Instant? = null,
    val principalMissionCompletedCount: Int = 0
)

// Update CharacterRepository to handle missions
interface CharacterRepository {
    suspend fun assignPrincipalMission(characterId: String, missionId: String): Result<Unit>
    suspend fun getPrincipalMissionProgress(characterId: String): Result<PrincipalMissionProgress?>
    suspend fun updateMissionStepProgress(characterId: String, stepId: String): Result<Unit>
    suspend fun markBossDefeated(characterId: String, missionId: String): Result<Unit>

    suspend fun discoverSecondaryMission(characterId: String, missionId: String): Result<Unit>
    suspend fun getActiveSecondaryMissions(characterId: String): Result<List<SecondaryMissionProgress>>
    suspend fun completeSecondaryMission(characterId: String, missionId: String, rewards: SecondaryMissionRewards): Result<Unit>

    suspend fun addLoreDiscovery(characterId: String, discovery: LoreDiscovery): Result<Unit>
    suspend fun getDiscoveredLore(characterId: String): Result<List<LoreDiscovery>>
}
```

### Repository Implementation Example

```kotlin
class SupabaseCharacterRepository : CharacterRepository {

    override suspend fun assignPrincipalMission(characterId: String, missionId: String): Result<Unit> {
        return try {
            // Update character
            supabase.from("characters")
                .update(mapOf(
                    "active_principal_mission_id" to missionId,
                    "principal_mission_started_at" to Instant.now().toString()
                ))
                .eq("id", characterId)
                .execute()

            // Create progress entry
            supabase.from("principal_mission_progress")
                .insert(mapOf(
                    "character_id" to characterId,
                    "mission_id" to missionId,
                    "status" to "in_progress"
                ))
                .execute()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMissionStepProgress(characterId: String, stepId: String): Result<Unit> {
        return try {
            // Use PostgreSQL array_append function
            supabase.from("principal_mission_progress")
                .update(mapOf(
                    "completed_step_ids" to "array_append(completed_step_ids, '$stepId')"
                ))
                .eq("character_id", characterId)
                .execute()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

## 10. Testing the Schema

### Test Script

```sql
-- Create a test character (assume UUID exists)
DO $$
DECLARE
    test_char_id UUID;
BEGIN
    -- Get a character ID (replace with actual ID)
    test_char_id := '00000000-0000-0000-0000-000000000000';

    -- Assign principal mission
    INSERT INTO principal_mission_progress (character_id, mission_id)
    VALUES (test_char_id, 'pm_warrior_1');

    -- Discover first step
    UPDATE principal_mission_progress
    SET completed_step_ids = array_append(completed_step_ids, 'pm_warrior_1_step_1')
    WHERE character_id = test_char_id AND mission_id = 'pm_warrior_1';

    -- Check progress
    RAISE NOTICE 'Progress: %', (
        SELECT progress_percentage
        FROM principal_mission_progress
        WHERE character_id = test_char_id AND mission_id = 'pm_warrior_1'
    );

    -- Discover secondary mission
    INSERT INTO secondary_mission_progress (character_id, mission_id, current_progress)
    VALUES (test_char_id, 'sm_001', 'Started exploring Heartlands');

    -- Add lore
    INSERT INTO lore_discoveries (
        character_id, lore_category, lore_title, lore_content, source_type, source_id
    )
    VALUES (
        test_char_id,
        'WORLD_HISTORY',
        'The Island of Aethermoor',
        'Aethermoor is a vast island continent...',
        'DEFAULT',
        NULL
    );

    RAISE NOTICE 'Test completed successfully!';
END $$;
```

---

## 11. Performance Considerations

- **Indexes**: All foreign keys and frequently queried columns are indexed
- **RLS**: Policies use efficient subqueries that leverage indexed columns
- **Array Operations**: `completed_step_ids` uses PostgreSQL array for efficient storage and querying
- **Triggers**: Progress percentage is auto-calculated to avoid expensive joins in queries

## 12. Future Enhancements

Consider adding these in future iterations:

1. `mission_rewards_history` table to track all rewards received
2. `mission_abandonment_log` to track when/why missions were abandoned
3. Materialized view for mission statistics and leaderboards
4. Partitioning `lore_discoveries` by date for very large datasets

---

**Document Version**: 1.0
**Last Updated**: 2025-11-15
**Compatible With**: Supabase PostgreSQL 15+
