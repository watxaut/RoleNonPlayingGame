package com.watxaut.rolenonplayinggame.data.repository

import android.util.Log
import com.watxaut.rolenonplayinggame.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
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

    override suspend fun getCurrentUserEmail(): String? {
        return try {
            auth.currentUserOrNull()?.email
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current user email", e)
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

    override suspend fun signUpWithEmail(email: String, password: String): Result<String> {
        return try {
            Log.d(TAG, "Signing up with email: $email")

            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val userId = auth.currentUserOrNull()?.id
            if (userId != null) {
                Log.d(TAG, "Email sign-up successful: $userId")
                Result.success(userId)
            } else {
                Log.e(TAG, "Email sign-up failed: user ID is null")
                Result.failure(Exception("Failed to get user ID after sign-up"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Email sign-up failed", e)
            val errorMessage = when {
                e.message?.contains("User already registered") == true ->
                    "This email is already registered. Please sign in instead."
                e.message?.contains("Email not confirmed") == true ->
                    "Please check your email to confirm your account before signing in."
                else -> e.message ?: "Sign up failed. Please try again."
            }
            Result.failure(Exception(errorMessage))
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<String> {
        return try {
            Log.d(TAG, "Signing in with email: $email")

            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val userId = auth.currentUserOrNull()?.id
            if (userId != null) {
                Log.d(TAG, "Email sign-in successful: $userId")
                Result.success(userId)
            } else {
                Log.e(TAG, "Email sign-in failed: user ID is null")
                Result.failure(Exception("Failed to get user ID after sign-in"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Email sign-in failed", e)
            val errorMessage = when {
                e.message?.contains("Email not confirmed") == true ->
                    "Your email is not confirmed. Please check the Supabase dashboard to confirm your account, or delete and recreate your account with email confirmation disabled."
                e.message?.contains("Invalid login credentials") == true ->
                    "Invalid email or password. Please try again."
                e.message?.contains("Email not found") == true ->
                    "No account found with this email. Please sign up first."
                else -> e.message ?: "Sign in failed. Please check your credentials."
            }
            Result.failure(Exception(errorMessage))
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
