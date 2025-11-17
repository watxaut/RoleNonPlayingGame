-- Migration: Enhanced social/multiplayer features
-- Date: 2025-11-17
-- Description: Adds public character profiles, encounter history, and social utilities

-- Public Character Profiles view (visible to all users)
CREATE OR REPLACE VIEW public_character_profiles AS
SELECT
    c.id,
    c.name,
    c.level,
    c.job_class,
    c.created_at,
    c.current_location,
    -- Stats (but not personality - keep that hidden)
    c.strength,
    c.intelligence,
    c.agility,
    c.luck,
    c.charisma,
    c.vitality,
    -- Achievements count
    (SELECT COUNT(*) FROM achievements WHERE character_id = c.id) as achievement_count,
    -- Combat stats
    COALESCE((
        SELECT COUNT(*)
        FROM activity_logs
        WHERE character_id = c.id
        AND activity_type = 'combat'
        AND description LIKE '%Defeated%'
    ), 0) as total_kills,
    -- Encounter history count
    (
        SELECT COUNT(*)
        FROM encounters
        WHERE (character1_id = c.id OR character2_id = c.id)
        AND status = 'completed'
    ) as total_encounters
FROM characters c;

-- Grant public read access to profiles
GRANT SELECT ON public_character_profiles TO authenticated;

COMMENT ON VIEW public_character_profiles IS 'Public character information visible to all players for social features';

-- Function to find nearby characters for encounters
CREATE OR REPLACE FUNCTION find_nearby_characters(
    p_character_id UUID,
    p_location VARCHAR(100),
    p_max_results INTEGER DEFAULT 10
)
RETURNS TABLE (
    character_id UUID,
    character_name VARCHAR(50),
    character_level INTEGER,
    job_class VARCHAR(50),
    personality_social FLOAT,
    last_update TIMESTAMPTZ
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        al.character_id,
        c.name,
        c.level,
        c.job_class,
        c.personality_social,
        al.last_update
    FROM active_locations al
    JOIN characters c ON c.id = al.character_id
    WHERE
        al.location = p_location
        AND al.character_id != p_character_id
        AND al.is_available_for_encounters = true
        AND al.last_update > NOW() - INTERVAL '5 minutes' -- Only active in last 5 minutes
        -- Don't match with characters from same user
        AND c.user_id != (SELECT user_id FROM characters WHERE id = p_character_id)
    ORDER BY al.last_update DESC
    LIMIT p_max_results;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

COMMENT ON FUNCTION find_nearby_characters IS 'Finds characters in the same location available for encounters';

-- Function to check compatibility for encounters
CREATE OR REPLACE FUNCTION calculate_encounter_compatibility(
    p_char1_id UUID,
    p_char2_id UUID
)
RETURNS FLOAT AS $$
DECLARE
    v_social1 FLOAT;
    v_social2 FLOAT;
    v_aggression1 FLOAT;
    v_aggression2 FLOAT;
    v_compatibility FLOAT;
BEGIN
    -- Get personality traits
    SELECT personality_social, personality_aggression
    INTO v_social1, v_aggression1
    FROM characters WHERE id = p_char1_id;

    SELECT personality_social, personality_aggression
    INTO v_social2, v_aggression2
    FROM characters WHERE id = p_char2_id;

    -- Calculate compatibility score (0-1)
    -- Higher if both are social, lower if one is aggressive and one is not
    v_compatibility := (v_social1 + v_social2) / 2.0;

    -- Reduce compatibility if aggression mismatch
    IF ABS(v_aggression1 - v_aggression2) > 0.5 THEN
        v_compatibility := v_compatibility * 0.7;
    END IF;

    RETURN v_compatibility;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

COMMENT ON FUNCTION calculate_encounter_compatibility IS 'Calculates compatibility score between two characters for encounters';

-- Function to get encounter history for a character
CREATE OR REPLACE FUNCTION get_encounter_history(
    p_character_id UUID,
    p_limit INTEGER DEFAULT 50
)
RETURNS TABLE (
    encounter_id UUID,
    other_character_id UUID,
    other_character_name VARCHAR(50),
    encounter_type VARCHAR(50),
    location VARCHAR(100),
    outcome JSONB,
    created_at TIMESTAMPTZ
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        e.id,
        CASE
            WHEN e.character1_id = p_character_id THEN e.character2_id
            ELSE e.character1_id
        END as other_char_id,
        CASE
            WHEN e.character1_id = p_character_id THEN c2.name
            ELSE c1.name
        END as other_char_name,
        e.encounter_type,
        e.location,
        e.outcome,
        e.created_at
    FROM encounters e
    LEFT JOIN characters c1 ON c1.id = e.character1_id
    LEFT JOIN characters c2 ON c2.id = e.character2_id
    WHERE
        (e.character1_id = p_character_id OR e.character2_id = p_character_id)
        AND e.status = 'completed'
    ORDER BY e.created_at DESC
    LIMIT p_limit;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

COMMENT ON FUNCTION get_encounter_history IS 'Retrieves encounter history for a character';

-- Function to update active location with automatic cleanup
CREATE OR REPLACE FUNCTION upsert_active_location(
    p_character_id UUID,
    p_location VARCHAR(100),
    p_available BOOLEAN DEFAULT true
)
RETURNS void AS $$
BEGIN
    INSERT INTO active_locations (character_id, location, is_available_for_encounters, last_update)
    VALUES (p_character_id, p_location, p_available, NOW())
    ON CONFLICT (character_id)
    DO UPDATE SET
        location = p_location,
        is_available_for_encounters = p_available,
        last_update = NOW();
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

COMMENT ON FUNCTION upsert_active_location IS 'Updates character location for encounter matching';

-- Function to clean up stale active locations (older than 10 minutes)
CREATE OR REPLACE FUNCTION cleanup_stale_locations()
RETURNS void AS $$
BEGIN
    DELETE FROM active_locations
    WHERE last_update < NOW() - INTERVAL '10 minutes';
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

COMMENT ON FUNCTION cleanup_stale_locations IS 'Removes stale location entries older than 10 minutes';

-- Create a scheduled job to clean up stale locations (requires pg_cron extension)
-- Note: This needs to be run manually via Supabase dashboard if pg_cron is available
-- For now, we'll document it as a manual maintenance task

-- Add encounter relationship counts to improve queries
CREATE INDEX IF NOT EXISTS idx_encounters_completed
    ON encounters(character1_id, character2_id)
    WHERE status = 'completed';

-- Grant necessary permissions
GRANT EXECUTE ON FUNCTION find_nearby_characters TO authenticated;
GRANT EXECUTE ON FUNCTION calculate_encounter_compatibility TO authenticated;
GRANT EXECUTE ON FUNCTION get_encounter_history TO authenticated;
GRANT EXECUTE ON FUNCTION upsert_active_location TO authenticated;
