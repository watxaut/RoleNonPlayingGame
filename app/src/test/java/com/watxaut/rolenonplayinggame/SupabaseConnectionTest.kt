package com.watxaut.rolenonplayinggame

import org.junit.Test
import org.junit.Assert.*

/**
 * Test to verify BuildConfig contains Supabase credentials.
 * This ensures local.properties is properly configured.
 */
class SupabaseConnectionTest {

    @Test
    fun `supabase URL should be configured`() {
        val url = BuildConfig.SUPABASE_URL
        assertNotNull("Supabase URL is null", url)
        assertNotEquals("Supabase URL not configured in local.properties", "", url)
        assertTrue(
            "Supabase URL should start with https://",
            url.startsWith("https://")
        )
        assertTrue(
            "Supabase URL should end with .supabase.co",
            url.endsWith(".supabase.co")
        )
    }

    @Test
    fun `supabase key should be configured`() {
        val key = BuildConfig.SUPABASE_KEY
        assertNotNull("Supabase key is null", key)
        assertNotEquals("Supabase key not configured in local.properties", "", key)
        assertTrue(
            "Supabase key should be a JWT token (at least 100 characters)",
            key.length > 100
        )
    }

    @Test
    fun `display supabase configuration for debugging`() {
        println("=== Supabase Configuration ===")
        println("URL: ${BuildConfig.SUPABASE_URL}")
        println("Key (first 20 chars): ${BuildConfig.SUPABASE_KEY.take(20)}...")
        println("==============================")
    }
}
