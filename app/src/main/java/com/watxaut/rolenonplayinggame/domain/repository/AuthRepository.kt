package com.watxaut.rolenonplayinggame.domain.repository

/**
 * Repository for authentication operations
 */
interface AuthRepository {
    /**
     * Get the current authenticated user ID (anonymous or permanent)
     * Returns null if not authenticated
     */
    suspend fun getCurrentUserId(): String?

    /**
     * Sign in anonymously
     * Creates an anonymous session if one doesn't exist
     * @return The user ID of the anonymous user
     */
    suspend fun signInAnonymously(): Result<String>

    /**
     * Check if user is currently authenticated (anonymous or permanent)
     */
    suspend fun isAuthenticated(): Boolean

    /**
     * Check if the current user is anonymous
     */
    suspend fun isAnonymous(): Boolean

    /**
     * Sign out the current user
     */
    suspend fun signOut(): Result<Unit>
}
