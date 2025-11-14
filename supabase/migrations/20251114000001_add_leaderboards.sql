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
    created_at,
    ROW_NUMBER() OVER (ORDER BY level DESC, experience DESC) as rank
FROM characters
WHERE level > 1 -- Exclude level 1 characters
ORDER BY level DESC, experience DESC
LIMIT 100;

-- Create index for faster queries
CREATE UNIQUE INDEX IF NOT EXISTS leaderboard_levels_id_idx ON leaderboard_levels(id);
CREATE INDEX IF NOT EXISTS leaderboard_levels_rank_idx ON leaderboard_levels(rank);

-- Leaderboard by Wealth (Gold)
CREATE MATERIALIZED VIEW IF NOT EXISTS leaderboard_wealth AS
SELECT
    id,
    name,
    level,
    gold,
    job_class,
    created_at,
    ROW_NUMBER() OVER (ORDER BY gold DESC) as rank
FROM characters
WHERE gold > 0
ORDER BY gold DESC
LIMIT 100;

-- Create index for faster queries
CREATE UNIQUE INDEX IF NOT EXISTS leaderboard_wealth_id_idx ON leaderboard_wealth(id);
CREATE INDEX IF NOT EXISTS leaderboard_wealth_rank_idx ON leaderboard_wealth(rank);

-- Leaderboard by Achievement Count
CREATE MATERIALIZED VIEW IF NOT EXISTS leaderboard_achievements AS
SELECT
    c.id,
    c.name,
    c.level,
    c.job_class,
    COUNT(a.id) as achievement_count,
    c.created_at,
    ROW_NUMBER() OVER (ORDER BY COUNT(a.id) DESC, c.level DESC) as rank
FROM characters c
LEFT JOIN achievements a ON c.id = a.character_id
GROUP BY c.id, c.name, c.level, c.job_class, c.created_at
HAVING COUNT(a.id) > 0
ORDER BY achievement_count DESC, c.level DESC
LIMIT 100;

-- Create index for faster queries
CREATE UNIQUE INDEX IF NOT EXISTS leaderboard_achievements_id_idx ON leaderboard_achievements(id);
CREATE INDEX IF NOT EXISTS leaderboard_achievements_rank_idx ON leaderboard_achievements(rank);

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
