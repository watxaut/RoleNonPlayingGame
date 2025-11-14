package com.watxaut.rolenonplayinggame.data.repository

import android.util.Log
import com.watxaut.rolenonplayinggame.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository using Supabase Auth
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : AuthRepository {

    companion object {
        private const val TAG = "AuthRepository"
    }

    private val auth: Auth
        get() = supabaseClient.auth

    override suspend fun getCurrentUserId(): String? {
        return try {
            auth.currentUserOrNull()?.id
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current user ID", e)
            null
        }
    }

    override suspend fun signInAnonymously(): Result<String> {
        return try {
            // Check if already signed in
            val currentUser = auth.currentUserOrNull()
            if (currentUser != null) {
                Log.d(TAG, "User already authenticated: ${currentUser.id}")
                return Result.success(currentUser.id)
            }

            // Sign in anonymously
            Log.d(TAG, "Signing in anonymously...")
            auth.signInAnonymously()

            val userId = auth.currentUserOrNull()?.id
            if (userId != null) {
                Log.d(TAG, "Anonymous sign-in successful: $userId")
                Result.success(userId)
            } else {
                Log.e(TAG, "Anonymous sign-in failed: user ID is null")
                Result.failure(Exception("Failed to get user ID after anonymous sign-in"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Anonymous sign-in failed", e)
            Result.failure(e)
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return try {
            auth.currentUserOrNull() != null
        } catch (e: Exception) {
            Log.e(TAG, "Error checking authentication status", e)
            false
        }
    }

    override suspend fun isAnonymous(): Boolean {
        return try {
            val user = auth.currentUserOrNull()
            if (user == null) {
                false
            } else {
                // Check if user is anonymous
                // Supabase stores this in user metadata as a JsonElement
                user.userMetadata?.get("is_anonymous")?.jsonPrimitive?.booleanOrNull ?: false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if user is anonymous", e)
            false
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Log.d(TAG, "User signed out successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Sign out failed", e)
            Result.failure(e)
        }
    }
}
