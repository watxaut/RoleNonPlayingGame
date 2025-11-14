-- Add leaderboard materialized views for Phase 3
-- These views are refreshed periodically to show top players

-- Leaderboard by Level and Experience
CREATE MATERIALIZED VIEW IF NOT EXISTS leaderboard_levels AS
SELECT
    id,
    name,
    level,
    experience,
    job_class,
    created_at
FROM characters
WHERE level > 1 -- Exclude level 1 characters
ORDER BY level DESC, experience DESC
LIMIT 100;

-- Create index for faster queries
CREATE UNIQUE INDEX IF NOT EXISTS leaderboard_levels_id_idx ON leaderboard_levels(id);
CREATE INDEX IF NOT EXISTS leaderboard_levels_sorting_idx ON leaderboard_levels(level DESC, experience DESC);

-- Leaderboard by Wealth (Gold)
CREATE MATERIALIZED VIEW IF NOT EXISTS leaderboard_wealth AS
SELECT
    id,
    name,
    level,
    gold,
    job_class,
    created_at
FROM characters
WHERE gold > 0
ORDER BY gold DESC
LIMIT 100;

-- Create index for faster queries
CREATE UNIQUE INDEX IF NOT EXISTS leaderboard_wealth_id_idx ON leaderboard_wealth(id);
CREATE INDEX IF NOT EXISTS leaderboard_wealth_sorting_idx ON leaderboard_wealth(gold DESC);

-- Leaderboard by Achievement Count
CREATE MATERIALIZED VIEW IF NOT EXISTS leaderboard_achievements AS
SELECT
    c.id,
    c.name,
    c.level,
    c.job_class,
    COUNT(a.id) as achievement_count,
    c.created_at
FROM characters c
LEFT JOIN achievements a ON c.id = a.character_id
GROUP BY c.id, c.name, c.level, c.job_class, c.created_at
HAVING COUNT(a.id) > 0
ORDER BY achievement_count DESC, c.level DESC
LIMIT 100;

-- Create index for faster queries
CREATE UNIQUE INDEX IF NOT EXISTS leaderboard_achievements_id_idx ON leaderboard_achievements(id);
CREATE INDEX IF NOT EXISTS leaderboard_achievements_sorting_idx ON leaderboard_achievements(achievement_count DESC, level DESC);

-- Function to refresh all leaderboards
CREATE OR REPLACE FUNCTION refresh_leaderboards()
RETURNS void
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY leaderboard_levels;
    REFRESH MATERIALIZED VIEW CONCURRENTLY leaderboard_wealth;
    REFRESH MATERIALIZED VIEW CONCURRENTLY leaderboard_achievements;
END;
$$;

-- Comment explaining the refresh strategy
COMMENT ON FUNCTION refresh_leaderboards() IS
'Refreshes all leaderboard materialized views. Should be called periodically (e.g., every 5-10 minutes) via a scheduled job.';
