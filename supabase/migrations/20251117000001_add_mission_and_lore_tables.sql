-- Migration: Add mission tracking and lore discovery tables
-- Date: 2025-11-17
-- Description: Adds tables for principal missions, secondary missions, and lore discoveries

-- Principal Mission Progress table
CREATE TABLE IF NOT EXISTS mission_progress (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    character_id UUID REFERENCES characters(id) ON DELETE CASCADE,
    mission_id VARCHAR(100) NOT NULL,
    completed_steps TEXT[] DEFAULT '{}',
    boss_encountered BOOLEAN DEFAULT FALSE,
    boss_defeated BOOLEAN DEFAULT FALSE,
    completed BOOLEAN DEFAULT FALSE,
    started_at TIMESTAMPTZ DEFAULT NOW(),
    completed_at TIMESTAMPTZ,

    UNIQUE(character_id, mission_id)
);

-- Secondary Mission Progress table
CREATE TABLE IF NOT EXISTS secondary_mission_progress (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    character_id UUID REFERENCES characters(id) ON DELETE CASCADE,
    mission_id VARCHAR(100) NOT NULL,
    progress_value INTEGER DEFAULT 0,
    target_value INTEGER NOT NULL,
    completed BOOLEAN DEFAULT FALSE,
    started_at TIMESTAMPTZ DEFAULT NOW(),
    completed_at TIMESTAMPTZ,

    UNIQUE(character_id, mission_id)
);

-- Lore Discoveries table
CREATE TABLE IF NOT EXISTS lore_discoveries (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    character_id UUID REFERENCES characters(id) ON DELETE CASCADE,
    lore_key VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL, -- world_history, regions, factions, mysteries, legendary_creatures, heros_journey, artifacts
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    discovery_source VARCHAR(100), -- mission_step, boss_defeat, location_discovery, item_found, npc_interaction
    discovered_at TIMESTAMPTZ DEFAULT NOW(),

    UNIQUE(character_id, lore_key)
);

-- Create indexes for efficient queries
CREATE INDEX IF NOT EXISTS idx_mission_progress_character
    ON mission_progress(character_id, completed);

CREATE INDEX IF NOT EXISTS idx_secondary_mission_progress_character
    ON secondary_mission_progress(character_id, completed);

CREATE INDEX IF NOT EXISTS idx_lore_character_category
    ON lore_discoveries(character_id, category, discovered_at DESC);

-- Row Level Security Policies

ALTER TABLE mission_progress ENABLE ROW LEVEL SECURITY;
ALTER TABLE secondary_mission_progress ENABLE ROW LEVEL SECURITY;
ALTER TABLE lore_discoveries ENABLE ROW LEVEL SECURITY;

-- Mission progress: Users can only see/modify their own character missions
CREATE POLICY mission_progress_select_policy ON mission_progress
    FOR SELECT USING (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

CREATE POLICY mission_progress_insert_policy ON mission_progress
    FOR INSERT WITH CHECK (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

CREATE POLICY mission_progress_update_policy ON mission_progress
    FOR UPDATE USING (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

-- Secondary mission progress policies
CREATE POLICY secondary_mission_progress_select_policy ON secondary_mission_progress
    FOR SELECT USING (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

CREATE POLICY secondary_mission_progress_insert_policy ON secondary_mission_progress
    FOR INSERT WITH CHECK (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

CREATE POLICY secondary_mission_progress_update_policy ON secondary_mission_progress
    FOR UPDATE USING (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

-- Lore discoveries policies
CREATE POLICY lore_discoveries_select_policy ON lore_discoveries
    FOR SELECT USING (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

CREATE POLICY lore_discoveries_insert_policy ON lore_discoveries
    FOR INSERT WITH CHECK (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

-- Grant permissions
GRANT ALL ON mission_progress TO authenticated;
GRANT ALL ON secondary_mission_progress TO authenticated;
GRANT ALL ON lore_discoveries TO authenticated;

-- Comments
COMMENT ON TABLE mission_progress IS 'Tracks progress on principal (job-class-specific) missions';
COMMENT ON TABLE secondary_mission_progress IS 'Tracks progress on secondary side quests';
COMMENT ON TABLE lore_discoveries IS 'Records lore fragments discovered by characters';
