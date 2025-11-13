-- Supabase Database Schema for Role Non-Playing Game
-- Based on TECHNICAL_IMPLEMENTATION_DOCUMENT.md Option 2: Hybrid Architecture

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Characters table
CREATE TABLE IF NOT EXISTS characters (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
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
    discovered_locations JSONB DEFAULT '["Heartlands - Starting Town"]',
    active_quests JSONB DEFAULT '[]',

    -- Constraints
    CONSTRAINT valid_level CHECK (level >= 1),
    CONSTRAINT valid_stats CHECK (
        strength >= 1 AND intelligence >= 1 AND agility >= 1 AND
        luck >= 1 AND charisma >= 1 AND vitality >= 1
    ),
    CONSTRAINT valid_hp CHECK (current_hp >= 0 AND current_hp <= max_hp),
    CONSTRAINT valid_personality CHECK (
        personality_courage BETWEEN 0 AND 1 AND
        personality_greed BETWEEN 0 AND 1 AND
        personality_curiosity BETWEEN 0 AND 1 AND
        personality_aggression BETWEEN 0 AND 1 AND
        personality_social BETWEEN 0 AND 1 AND
        personality_impulsive BETWEEN 0 AND 1
    )
);

-- Activity logs table
CREATE TABLE IF NOT EXISTS activity_logs (
    id BIGSERIAL PRIMARY KEY,
    character_id UUID REFERENCES characters(id) ON DELETE CASCADE,
    timestamp TIMESTAMPTZ DEFAULT NOW(),
    activity_type VARCHAR(50), -- combat, exploration, social, quest, etc.
    description TEXT,
    rewards JSONB, -- {xp: 50, gold: 20, items: [...]}
    metadata JSONB, -- additional context
    is_major_event BOOLEAN DEFAULT FALSE
);

-- Create index for efficient queries
CREATE INDEX IF NOT EXISTS idx_activity_character_timestamp
    ON activity_logs(character_id, timestamp DESC);

-- Encounters table (for social features)
CREATE TABLE IF NOT EXISTS encounters (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    character1_id UUID REFERENCES characters(id) ON DELETE CASCADE,
    character2_id UUID REFERENCES characters(id) ON DELETE CASCADE,
    location VARCHAR(100),
    encounter_type VARCHAR(50), -- greeting, trade, party, combat, ignore
    status VARCHAR(50), -- initiated, negotiating, completed, failed
    outcome JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    completed_at TIMESTAMPTZ
);

-- Create indexes for encounter queries
CREATE INDEX IF NOT EXISTS idx_active_encounters
    ON encounters(status, created_at) WHERE status IN ('initiated', 'negotiating');

CREATE INDEX IF NOT EXISTS idx_character_encounters
    ON encounters(character1_id, character2_id);

-- Active locations table (for proximity matching)
CREATE TABLE IF NOT EXISTS active_locations (
    character_id UUID PRIMARY KEY REFERENCES characters(id) ON DELETE CASCADE,
    location VARCHAR(100),
    last_update TIMESTAMPTZ DEFAULT NOW(),
    is_available_for_encounters BOOLEAN DEFAULT TRUE
);

-- Create index for location lookup
CREATE INDEX IF NOT EXISTS idx_location_lookup
    ON active_locations(location, last_update);

-- Achievements table
CREATE TABLE IF NOT EXISTS achievements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    character_id UUID REFERENCES characters(id) ON DELETE CASCADE,
    achievement_key VARCHAR(100),
    unlocked_at TIMESTAMPTZ DEFAULT NOW(),
    progress JSONB, -- for multi-step achievements

    UNIQUE(character_id, achievement_key)
);

-- Create index for achievement queries
CREATE INDEX IF NOT EXISTS idx_character_achievements
    ON achievements(character_id, unlocked_at DESC);

-- Leaderboards (materialized views, refreshed periodically)
CREATE MATERIALIZED VIEW IF NOT EXISTS leaderboard_levels AS
SELECT id, name, level, job_class, experience
FROM characters
ORDER BY level DESC, experience DESC
LIMIT 100;

CREATE MATERIALIZED VIEW IF NOT EXISTS leaderboard_wealth AS
SELECT id, name, level, job_class, gold
FROM characters
ORDER BY gold DESC
LIMIT 100;

-- Create indexes on materialized views
CREATE INDEX IF NOT EXISTS idx_leaderboard_levels_level
    ON leaderboard_levels(level DESC, experience DESC);

CREATE INDEX IF NOT EXISTS idx_leaderboard_wealth_gold
    ON leaderboard_wealth(gold DESC);

-- Row Level Security (RLS) Policies

-- Enable RLS on all tables
ALTER TABLE characters ENABLE ROW LEVEL SECURITY;
ALTER TABLE activity_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE encounters ENABLE ROW LEVEL SECURITY;
ALTER TABLE active_locations ENABLE ROW LEVEL SECURITY;
ALTER TABLE achievements ENABLE ROW LEVEL SECURITY;

-- Characters: Users can only see/modify their own characters
CREATE POLICY characters_select_policy ON characters
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY characters_insert_policy ON characters
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY characters_update_policy ON characters
    FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY characters_delete_policy ON characters
    FOR DELETE USING (auth.uid() = user_id);

-- Activity logs: Users can only see logs for their characters
CREATE POLICY activity_logs_select_policy ON activity_logs
    FOR SELECT USING (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

CREATE POLICY activity_logs_insert_policy ON activity_logs
    FOR INSERT WITH CHECK (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

-- Encounters: Users can see encounters involving their characters
CREATE POLICY encounters_select_policy ON encounters
    FOR SELECT USING (
        character1_id IN (SELECT id FROM characters WHERE user_id = auth.uid()) OR
        character2_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

-- Active locations: Users can only update their own character locations
CREATE POLICY active_locations_select_policy ON active_locations
    FOR SELECT USING (true); -- Public read for proximity matching

CREATE POLICY active_locations_insert_policy ON active_locations
    FOR INSERT WITH CHECK (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

CREATE POLICY active_locations_update_policy ON active_locations
    FOR UPDATE USING (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

-- Achievements: Users can see achievements for their characters
CREATE POLICY achievements_select_policy ON achievements
    FOR SELECT USING (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

CREATE POLICY achievements_insert_policy ON achievements
    FOR INSERT WITH CHECK (
        character_id IN (SELECT id FROM characters WHERE user_id = auth.uid())
    );

-- Functions for common operations

-- Function to refresh leaderboards
CREATE OR REPLACE FUNCTION refresh_leaderboards()
RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW leaderboard_levels;
    REFRESH MATERIALIZED VIEW leaderboard_wealth;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Function to update character's last active time
CREATE OR REPLACE FUNCTION update_last_active()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_active_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to automatically update last_active_at
CREATE TRIGGER update_character_last_active
BEFORE UPDATE ON characters
FOR EACH ROW
EXECUTE FUNCTION update_last_active();

-- Function to clean up old activity logs (keep last 500 per character)
CREATE OR REPLACE FUNCTION cleanup_old_activity_logs()
RETURNS void AS $$
BEGIN
    DELETE FROM activity_logs
    WHERE id IN (
        SELECT id FROM (
            SELECT id,
                   ROW_NUMBER() OVER (PARTITION BY character_id ORDER BY timestamp DESC) as rn
            FROM activity_logs
        ) t
        WHERE rn > 500
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Grant necessary permissions
GRANT USAGE ON SCHEMA public TO authenticated;
GRANT ALL ON ALL TABLES IN SCHEMA public TO authenticated;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO authenticated;

-- Comments for documentation
COMMENT ON TABLE characters IS 'Main table storing player characters';
COMMENT ON TABLE activity_logs IS 'Stores character activity history (last 500 entries per character)';
COMMENT ON TABLE encounters IS 'Records social interactions between characters';
COMMENT ON TABLE active_locations IS 'Tracks character locations for proximity matching';
COMMENT ON TABLE achievements IS 'Stores unlocked achievements for characters';
COMMENT ON MATERIALIZED VIEW leaderboard_levels IS 'Top 100 characters by level';
COMMENT ON MATERIALIZED VIEW leaderboard_wealth IS 'Top 100 characters by gold';
