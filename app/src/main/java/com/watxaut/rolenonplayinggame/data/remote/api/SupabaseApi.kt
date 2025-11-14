package com.watxaut.rolenonplayinggame.data.remote.api

import com.watxaut.rolenonplayinggame.data.remote.dto.OfflineSimulationRequest
import com.watxaut.rolenonplayinggame.data.remote.dto.OfflineSimulationResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API client for Supabase Edge Functions
 */
@Singleton
class SupabaseApi @Inject constructor(
    private val httpClient: HttpClient,
    private val supabaseConfig: SupabaseConfig
) {

    /**
     * Call the offline-simulation Edge Function
     * @param characterId The character ID to simulate
     * @return OfflineSimulationResponse with simulation results
     */
    suspend fun runOfflineSimulation(characterId: String): Result<OfflineSimulationResponse> {
        return try {
            val response = httpClient.post {
                url("${supabaseConfig.url}/functions/v1/offline-simulation")
                contentType(ContentType.Application.Json)
                headers {
                    append("Authorization", "Bearer ${supabaseConfig.anonKey}")
                    // TODO: Add user auth token when available
                    // append("Authorization", "Bearer ${userAuthToken}")
                }
                setBody(OfflineSimulationRequest(characterId))
            }

            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
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
