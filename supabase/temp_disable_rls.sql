-- Temporary fix for development: Allow anonymous character creation
-- Run this in Supabase SQL Editor to allow character creation without authentication
-- IMPORTANT: Remove these policies in production!

-- First, create a dummy user for development
-- This user_id must match the one in the app: 00000000-0000-0000-0000-000000000000

-- Drop existing restrictive policies
DROP POLICY IF EXISTS characters_insert_policy ON characters;
DROP POLICY IF EXISTS characters_update_policy ON characters;
DROP POLICY IF EXISTS characters_select_policy ON characters;
DROP POLICY IF EXISTS characters_delete_policy ON characters;

-- Create permissive policies for development
-- These allow operations for the anonymous UUID

-- Allow SELECT for anyone (you can see all characters)
CREATE POLICY characters_select_policy_dev ON characters
    FOR SELECT USING (true);

-- Allow INSERT for anyone
CREATE POLICY characters_insert_policy_dev ON characters
    FOR INSERT WITH CHECK (true);

-- Allow UPDATE for anyone
CREATE POLICY characters_update_policy_dev ON characters
    FOR UPDATE USING (true);

-- Allow DELETE for anyone
CREATE POLICY characters_delete_policy_dev ON characters
    FOR DELETE USING (true);

-- Alternative: If you want to keep RLS but allow the anonymous UUID
-- Uncomment these instead of the above policies:

/*
-- Allow operations for the anonymous user UUID
CREATE POLICY characters_select_policy_dev ON characters
    FOR SELECT USING (
        user_id = '00000000-0000-0000-0000-000000000000'::uuid OR
        auth.uid() = user_id
    );

CREATE POLICY characters_insert_policy_dev ON characters
    FOR INSERT WITH CHECK (
        user_id = '00000000-0000-0000-0000-000000000000'::uuid OR
        auth.uid() = user_id
    );

CREATE POLICY characters_update_policy_dev ON characters
    FOR UPDATE USING (
        user_id = '00000000-0000-0000-0000-000000000000'::uuid OR
        auth.uid() = user_id
    );

CREATE POLICY characters_delete_policy_dev ON characters
    FOR DELETE USING (
        user_id = '00000000-0000-0000-0000-000000000000'::uuid OR
        auth.uid() = user_id
    );
*/

-- Also temporarily allow inserts without the foreign key constraint
-- by making user_id nullable or removing the constraint
-- NOTE: This is just for development. Re-enable for production!

-- Remove foreign key constraint (optional, for maximum compatibility)
ALTER TABLE characters DROP CONSTRAINT IF EXISTS characters_user_id_fkey;

-- Make user_id nullable temporarily (optional)
-- ALTER TABLE characters ALTER COLUMN user_id DROP NOT NULL;

-- Success message
SELECT 'RLS policies updated for development. Characters can now be created without authentication.' AS status;
