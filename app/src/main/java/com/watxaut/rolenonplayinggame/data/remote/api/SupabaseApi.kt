package com.watxaut.rolenonplayinggame.data.remote.api

import com.watxaut.rolenonplayinggame.data.remote.dto.OfflineSimulationRequest
import com.watxaut.rolenonplayinggame.data.remote.dto.OfflineSimulationResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API client for Supabase Edge Functions
 */
@Singleton
class SupabaseApi @Inject constructor(
    private val httpClient: HttpClient,
    private val supabaseConfig: SupabaseConfig,
    private val supabaseClient: SupabaseClient
) {

    /**
     * Call the offline-simulation Edge Function
     * @param characterId The character ID to simulate
     * @return OfflineSimulationResponse with simulation results
     */
    suspend fun runOfflineSimulation(characterId: String): Result<OfflineSimulationResponse> {
        return try {
            // Ensure we have a valid session
            var session = supabaseClient.auth.currentSessionOrNull()

            if (session == null) {
                // Try to load session from storage
                try {
                    supabaseClient.auth.loadFromStorage()
                    session = supabaseClient.auth.currentSessionOrNull()
                } catch (e: Exception) {
                    return Result.failure(Exception("Session expired. Please log in again."))
                }

                if (session == null) {
                    return Result.failure(Exception("Session expired. Please log in again."))
                }
            }

            // Refresh the session to ensure valid token
            try {
                supabaseClient.auth.refreshCurrentSession()
                session = supabaseClient.auth.currentSessionOrNull()
                    ?: return Result.failure(Exception("Session refresh failed."))
            } catch (e: Exception) {
                return Result.failure(Exception("Session refresh failed: ${e.message}"))
            }

            // Call the Edge Function
            val response: HttpResponse = httpClient.post {
                url("${supabaseConfig.url}/functions/v1/offline-simulation")
                contentType(ContentType.Application.Json)
                headers {
                    append("Authorization", "Bearer ${session.accessToken}")
                    append("apikey", supabaseConfig.anonKey)
                }
                setBody(OfflineSimulationRequest(characterId))
            }

            // Check HTTP status before deserializing
            if (response.status.value in 200..299) {
                Result.success(response.body())
            } else {
                val errorBody = response.body<String>()
                Result.failure(
                    Exception("Offline simulation failed: ${response.status.value} - $errorBody")
                )
            }
        } catch (e: Exception) {
            Result.failure(Exception("Offline simulation failed: ${e.message}", e))
        }
    }

    /**
     * Sync activity logs to Supabase
     * @param characterId The character ID
     * @param activities List of activities to sync
     */
    suspend fun syncActivityLogs(
        characterId: String,
        activities: List<Any>
    ): Result<Unit> {
        return try {
            // TODO: Implement activity log sync via Supabase REST API
            // This will be used to upload local activities to server
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Supabase configuration
 * TODO: Move to BuildConfig or environment variables
 */
data class SupabaseConfig(
    val url: String = "https://your-project.supabase.co",
    val anonKey: String = "your-anon-key"
)
