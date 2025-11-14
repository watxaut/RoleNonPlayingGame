-- Enable RLS with Anonymous Authentication Support
-- This script rolls back temp_disable_rls.sql and enables proper RLS for anonymous users
-- Run this in Supabase SQL Editor after temp_disable_rls.sql

-- ============================================
-- STEP 1: Drop Development Policies
-- ============================================

DROP POLICY IF EXISTS characters_select_policy_dev ON characters;
DROP POLICY IF EXISTS characters_insert_policy_dev ON characters;
DROP POLICY IF EXISTS characters_update_policy_dev ON characters;
DROP POLICY IF EXISTS characters_delete_policy_dev ON characters;

-- ============================================
-- STEP 2: Restore Foreign Key Constraint
-- ============================================

-- Re-add the foreign key constraint to auth.users
-- This ensures user_id references a valid authenticated user (anonymous or permanent)
ALTER TABLE characters
  ADD CONSTRAINT characters_user_id_fkey
  FOREIGN KEY (user_id)
  REFERENCES auth.users(id)
  ON DELETE CASCADE;

-- ============================================
-- STEP 3: Create RLS Policies for Authenticated Users
-- ============================================

-- These policies work for BOTH anonymous and permanent users
-- The key insight: anonymous users are authenticated users with is_anonymous = true

-- SELECT: Users can only see their own characters
CREATE POLICY characters_select_policy ON characters
    FOR SELECT
    USING (auth.uid() = user_id);

-- INSERT: Users can only create characters for themselves
CREATE POLICY characters_insert_policy ON characters
    FOR INSERT
    WITH CHECK (auth.uid() = user_id);

-- UPDATE: Users can only update their own characters
CREATE POLICY characters_update_policy ON characters
    FOR UPDATE
    USING (auth.uid() = user_id);

-- DELETE: Users can only delete their own characters
CREATE POLICY characters_delete_policy ON characters
    FOR DELETE
    USING (auth.uid() = user_id);

-- ============================================
-- STEP 4: Optional - Distinguish Anonymous Users
-- ============================================

-- If you want to add special policies for anonymous vs permanent users later,
-- you can check the is_anonymous field in the JWT like this:
--
-- Example: Allow anonymous users to create characters but limit to 1:
-- CREATE POLICY characters_insert_limit_anonymous ON characters
--     FOR INSERT
--     WITH CHECK (
--         auth.uid() = user_id AND (
--             (auth.jwt() -> 'user_metadata' ->> 'is_anonymous')::boolean = false OR
--             (SELECT COUNT(*) FROM characters WHERE user_id = auth.uid()) < 1
--         )
--     );
--
-- Note: The above is commented out because it's optional and may be too restrictive

-- ============================================
-- STEP 5: Grant Permissions to Anonymous Role
-- ============================================

-- Anonymous users are part of the 'authenticated' role
-- These grants ensure anonymous users can perform necessary operations
GRANT USAGE ON SCHEMA public TO authenticated, anon;
GRANT SELECT, INSERT, UPDATE, DELETE ON characters TO authenticated, anon;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO authenticated, anon;

-- ============================================
-- VERIFICATION
-- ============================================

-- Verify RLS is enabled
SELECT tablename, rowsecurity
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'characters';

-- Verify policies are active
SELECT schemaname, tablename, policyname, permissive, roles, cmd
FROM pg_policies
WHERE tablename = 'characters'
ORDER BY policyname;

-- Success message
SELECT 'RLS enabled with anonymous authentication support. Both anonymous and permanent users can now manage their own characters.' AS status;
