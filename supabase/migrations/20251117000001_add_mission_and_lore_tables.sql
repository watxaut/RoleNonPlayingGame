-- Migration: Add mission tracking and lore discovery tables
-- Date: 2025-11-17
-- Description: Adds tables for principal missions, secondary missions, and lore discoveries

-- Principal Mission Progress table
CREATE TABLE IF NOT EXISTS principal_mission_progress (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    character_id UUID REFERENCES characters(id) ON DELETE CASCADE,
    mission_id VARCHAR(100) NOT NULL,
    status VARCHAR(50) DEFAULT 'in_progress', -- 'in_progress', 'completed', 'abandoned'
    completed_step_ids TEXT[] DEFAULT '{}',
    boss_encountered BOOLEAN DEFAULT FALSE,
    boss_defeated BOOLEAN DEFAULT FALSE,
    started_at TIMESTAMPTZ DEFAULT NOW(),
    completed_at TIMESTAMPTZ,
    last_progress_at TIMESTAMPTZ DEFAULT NOW(),
    progress_percentage INTEGER DEFAULT 0,

    UNIQUE(character_id, mission_id)
);

-- Secondary Mission Progress table
CREATE TABLE IF NOT EXISTS secondary_mission_progress (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    character_id UUID REFERENCES characters(id) ON DELETE CASCADE,
    mission_id VARCHAR(100) NOT NULL,
    status VARCHAR(50) DEFAULT 'ongoing', -- 'ongoing', 'completed'
    current_progress TEXT,
    discovered_at TIMESTAMPTZ DEFAULT NOW(),
    completed_at TIMESTAMPTZ,
    reward_experience BIGINT,
    reward_gold INTEGER,
    reward_equipment VARCHAR(100),

    UNIQUE(character_id, mission_id)
);

-- Lore Discoveries table
CREATE TABLE IF NOT EXISTS lore_discoveries (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    character_id UUID REFERENCES characters(id) ON DELETE CASCADE,
    lore_category VARCHAR(100) NOT NULL, -- world_history, regions, factions, mysteries, legendary_creatures, heros_journey, artifacts
    lore_title TEXT NOT NULL,
    lore_content TEXT NOT NULL,
    source_type VARCHAR(100), -- mission_step, boss_defeat, location_discovery, item_found, npc_interaction
    source_id VARCHAR(100), -- ID of the source (mission ID, boss ID, etc.)
    discovered_at TIMESTAMPTZ DEFAULT NOW(),

    UNIQUE(character_id, lore_title)
);

-- Create indexes for efficient queries
CREATE INDEX IF NOT EXISTS idx_principal_mission_progress_character
    ON principal_mission_progress(character_id, status);

CREATE INDEX IF NOT EXISTS idx_secondary_mission_progress_character
    ON secondary_mission_progress(character_id, status);

CREATE INDEX IF NOT EXISTS idx_lore_character_category
    ON lore_discoveries(character_id, lore_category, discovered_at DESC);

-- Row Level Security Policies

ALTER TABLE principal_mission_progress ENABLE ROW LEVEL SECURITY;
ALTER TABLE secondary_mission_progress ENABLE ROW LEVEL SECURITY;
ALTER TABLE lore_discoveries ENABLE ROW LEVEL SECURITY;

-- Principal mission progress: Users can only see/modify their own character missions
CREATE POLICY principal_mission_progress_select_policy ON principal_mission_progress
    FOR SELECT USING (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

CREATE POLICY principal_mission_progress_insert_policy ON principal_mission_progress
    FOR INSERT WITH CHECK (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

CREATE POLICY principal_mission_progress_update_policy ON principal_mission_progress
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
GRANT ALL ON principal_mission_progress TO authenticated;
GRANT ALL ON secondary_mission_progress TO authenticated;
GRANT ALL ON lore_discoveries TO authenticated;

-- Comments
COMMENT ON TABLE principal_mission_progress IS 'Tracks progress on principal (job-class-specific) missions';
COMMENT ON TABLE secondary_mission_progress IS 'Tracks progress on secondary side quests';
COMMENT ON TABLE lore_discoveries IS 'Records lore fragments discovered by characters';
