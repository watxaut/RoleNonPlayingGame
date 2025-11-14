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
     * Get the current user's email
     * Returns null if not authenticated or anonymous
     */
    suspend fun getCurrentUserEmail(): String?

    /**
     * Sign in anonymously
     * Creates an anonymous session if one doesn't exist
     * @return The user ID of the anonymous user
     */
    suspend fun signInAnonymously(): Result<String>

    /**
     * Sign up with email and password
     * @param email User's email address
     * @param password User's password
     * @return The user ID of the created user
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<String>

    /**
     * Sign in with email and password
     * @param email User's email address
     * @param password User's password
     * @return The user ID of the authenticated user
     */
    suspend fun signInWithEmail(email: String, password: String): Result<String>

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
