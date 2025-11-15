package com.watxaut.rolenonplayinggame.data.repository

import android.util.Log
import com.watxaut.rolenonplayinggame.BuildConfig
import com.watxaut.rolenonplayinggame.data.remote.dto.LoreDiscoveryDto
import com.watxaut.rolenonplayinggame.data.remote.dto.PrincipalMissionProgressDto
import com.watxaut.rolenonplayinggame.data.remote.dto.SecondaryMissionProgressDto
import com.watxaut.rolenonplayinggame.domain.model.LoreDiscovery
import com.watxaut.rolenonplayinggame.domain.model.PrincipalMissionProgress
import com.watxaut.rolenonplayinggame.domain.model.SecondaryMissionProgress
import com.watxaut.rolenonplayinggame.domain.repository.MissionProgressRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import javax.inject.Inject

/**
 * Implementation of MissionProgressRepository using Supabase
 */
class MissionProgressRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : MissionProgressRepository {

    companion object {
        private const val TAG = "MissionProgressRepo"
        private const val TABLE_PRINCIPAL = "principal_mission_progress"
        private const val TABLE_SECONDARY = "secondary_mission_progress"
        private const val TABLE_LORE = "lore_discoveries"

        private fun logDebug(message: String) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, message)
            }
        }

        private fun logError(message: String, throwable: Throwable? = null) {
            if (throwable != null) {
                Log.e(TAG, message, throwable)
            } else {
                Log.e(TAG, message)
            }
        }
    }

    // ============================================================
    // PRINCIPAL MISSION PROGRESS
    // ============================================================

    override suspend fun getActivePrincipalMission(characterId: String): Result<PrincipalMissionProgress?> {
        return try {
            logDebug("Fetching active principal mission for character: $characterId")
            val response = supabaseClient
                .from(TABLE_PRINCIPAL)
                .select {
                    filter {
                        eq("character_id", characterId)
                        eq("status", "in_progress")
                    }
                }
                .decodeSingle<PrincipalMissionProgressDto>()

            Result.success(response.toDomainModel())
        } catch (e: Exception) {
            // No active mission found is not an error
            if (e.message?.contains("JSON object") == true || e.message?.contains("404") == true) {
                logDebug("No active principal mission found for character: $characterId")
                Result.success(null)
            } else {
                logError("Failed to fetch active principal mission", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun assignPrincipalMission(characterId: String, missionId: String): Result<Unit> {
        return try {
            logDebug("Assigning principal mission $missionId to character $characterId")

            val dto = PrincipalMissionProgressDto(
                characterId = characterId,
                missionId = missionId,
                status = "in_progress",
                startedAt = Instant.now().toString()
            )

            supabaseClient
                .from(TABLE_PRINCIPAL)
                .insert(dto)

            logDebug("Principal mission assigned successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            logError("Failed to assign principal mission", e)
            Result.failure(e)
        }
    }

    override suspend fun completeStory(
        characterId: String,
        missionId: String,
        stepId: String
    ): Result<Unit> {
        return try {
            logDebug("Completing step $stepId for mission $missionId")

            // Fetch current progress
            val currentProgress = supabaseClient
                .from(TABLE_PRINCIPAL)
                .select {
                    filter {
                        eq("character_id", characterId)
                        eq("mission_id", missionId)
                    }
                }
                .decodeSingle<PrincipalMissionProgressDto>()

            // Add step to completed steps if not already present
            val updatedSteps = currentProgress.completedStepIds.toMutableList()
            if (!updatedSteps.contains(stepId)) {
                updatedSteps.add(stepId)
            }

            // Update with new step
            supabaseClient
                .from(TABLE_PRINCIPAL)
                .update(
                    {
                        set("completed_step_ids", updatedSteps)
                        set("last_progress_at", Instant.now().toString())
                    }
                ) {
                    filter {
                        eq("character_id", characterId)
                        eq("mission_id", missionId)
                    }
                }

            logDebug("Step completed successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            logError("Failed to complete step", e)
            Result.failure(e)
        }
    }

    override suspend fun markBossEncountered(characterId: String, missionId: String): Result<Unit> {
        return try {
            logDebug("Marking boss encountered for mission $missionId")

            supabaseClient
                .from(TABLE_PRINCIPAL)
                .update(
                    {
                        set("boss_encountered", true)
                        set("last_progress_at", Instant.now().toString())
                    }
                ) {
                    filter {
                        eq("character_id", characterId)
                        eq("mission_id", missionId)
                    }
                }

            logDebug("Boss encounter marked")
            Result.success(Unit)
        } catch (e: Exception) {
            logError("Failed to mark boss encountered", e)
            Result.failure(e)
        }
    }

    override suspend fun markBossDefeated(characterId: String, missionId: String): Result<Unit> {
        return try {
            logDebug("Marking boss defeated for mission $missionId")

            supabaseClient
                .from(TABLE_PRINCIPAL)
                .update(
                    {
                        set("boss_defeated", true)
                        set("boss_encountered", true)
                        set("status", "completed")
                        set("completed_at", Instant.now().toString())
                        set("last_progress_at", Instant.now().toString())
                    }
                ) {
                    filter {
                        eq("character_id", characterId)
                        eq("mission_id", missionId)
                    }
                }

            logDebug("Boss defeated, mission completed")
            Result.success(Unit)
        } catch (e: Exception) {
            logError("Failed to mark boss defeated", e)
            Result.failure(e)
        }
    }

    override suspend fun getCompletedPrincipalMissions(characterId: String): Result<List<PrincipalMissionProgress>> {
        return try {
            logDebug("Fetching completed principal missions for character: $characterId")
            val response = supabaseClient
                .from(TABLE_PRINCIPAL)
                .select {
                    filter {
                        eq("character_id", characterId)
                        eq("status", "completed")
                    }
                }
                .decodeList<PrincipalMissionProgressDto>()

            Result.success(response.map { it.toDomainModel() })
        } catch (e: Exception) {
            logError("Failed to fetch completed principal missions", e)
            Result.failure(e)
        }
    }

    override fun getPrincipalMissionProgressFlow(characterId: String): Flow<PrincipalMissionProgress?> = flow {
        // For now, just emit the current state
        // TODO: Implement realtime subscriptions when needed
        val result = getActivePrincipalMission(characterId)
        result.getOrNull()?.let { emit(it) }
    }

    // ============================================================
    // SECONDARY MISSION PROGRESS
    // ============================================================

    override suspend fun getActiveSecondaryMissions(characterId: String): Result<List<SecondaryMissionProgress>> {
        return try {
            logDebug("Fetching active secondary missions for character: $characterId")
            val response = supabaseClient
                .from(TABLE_SECONDARY)
                .select {
                    filter {
                        eq("character_id", characterId)
                        eq("status", "ongoing")
                    }
                }
                .decodeList<SecondaryMissionProgressDto>()

            Result.success(response.map { it.toDomainModel() })
        } catch (e: Exception) {
            logError("Failed to fetch active secondary missions", e)
            Result.failure(e)
        }
    }

    override suspend fun discoverSecondaryMission(characterId: String, missionId: String): Result<Unit> {
        return try {
            logDebug("Discovering secondary mission $missionId for character $characterId")

            val dto = SecondaryMissionProgressDto(
                characterId = characterId,
                missionId = missionId,
                status = "ongoing",
                discoveredAt = Instant.now().toString()
            )

            supabaseClient
                .from(TABLE_SECONDARY)
                .insert(dto)

            logDebug("Secondary mission discovered")
            Result.success(Unit)
        } catch (e: Exception) {
            logError("Failed to discover secondary mission", e)
            Result.failure(e)
        }
    }

    override suspend fun updateSecondaryMissionProgress(
        characterId: String,
        missionId: String,
        progressText: String
    ): Result<Unit> {
        return try {
            logDebug("Updating secondary mission progress: $missionId")

            supabaseClient
                .from(TABLE_SECONDARY)
                .update(
                    {
                        set("current_progress", progressText)
                    }
                ) {
                    filter {
                        eq("character_id", characterId)
                        eq("mission_id", missionId)
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            logError("Failed to update secondary mission progress", e)
            Result.failure(e)
        }
    }

    override suspend fun completeSecondaryMission(
        characterId: String,
        missionId: String,
        rewardExperience: Long,
        rewardGold: Int,
        rewardEquipment: String?
    ): Result<Unit> {
        return try {
            logDebug("Completing secondary mission: $missionId")

            supabaseClient
                .from(TABLE_SECONDARY)
                .update(
                    {
                        set("status", "completed")
                        set("completed_at", Instant.now().toString())
                        set("reward_experience", rewardExperience)
                        set("reward_gold", rewardGold)
                        rewardEquipment?.let { set("reward_equipment", it) }
                    }
                ) {
                    filter {
                        eq("character_id", characterId)
                        eq("mission_id", missionId)
                    }
                }

            logDebug("Secondary mission completed")
            Result.success(Unit)
        } catch (e: Exception) {
            logError("Failed to complete secondary mission", e)
            Result.failure(e)
        }
    }

    override suspend fun getCompletedSecondaryMissions(characterId: String): Result<List<SecondaryMissionProgress>> {
        return try {
            logDebug("Fetching completed secondary missions for character: $characterId")
            val response = supabaseClient
                .from(TABLE_SECONDARY)
                .select {
                    filter {
                        eq("character_id", characterId)
                        eq("status", "completed")
                    }
                }
                .decodeList<SecondaryMissionProgressDto>()

            Result.success(response.map { it.toDomainModel() })
        } catch (e: Exception) {
            logError("Failed to fetch completed secondary missions", e)
            Result.failure(e)
        }
    }

    override fun getSecondaryMissionProgressFlow(characterId: String): Flow<List<SecondaryMissionProgress>> = flow {
        // For now, just emit the current state
        // TODO: Implement realtime subscriptions when needed
        val result = getActiveSecondaryMissions(characterId)
        result.getOrNull()?.let { emit(it) }
    }

    // ============================================================
    // LORE DISCOVERIES
    // ============================================================

    override suspend fun addLoreDiscovery(loreDiscovery: LoreDiscovery): Result<Unit> {
        return try {
            // Extract characterId from loreDiscovery.id (format: "characterId_loreId")
            // Actually, we need characterId to be passed separately
            // For now, we'll assume the id contains the characterId
            // This should be refactored to accept characterId as a parameter
            logError("addLoreDiscovery called but characterId extraction not implemented")
            Result.failure(IllegalArgumentException("characterId must be provided separately"))
        } catch (e: Exception) {
            logError("Failed to add lore discovery", e)
            Result.failure(e)
        }
    }

    /**
     * Add lore discovery with explicit character ID
     */
    suspend fun addLoreDiscovery(characterId: String, loreDiscovery: LoreDiscovery): Result<Unit> {
        return try {
            logDebug("Adding lore discovery for character $characterId: ${loreDiscovery.title}")

            val dto = LoreDiscoveryDto.fromDomainModel(characterId, loreDiscovery)

            supabaseClient
                .from(TABLE_LORE)
                .insert(dto)

            logDebug("Lore discovery added")
            Result.success(Unit)
        } catch (e: Exception) {
            // Duplicate lore is not an error (unique constraint)
            if (e.message?.contains("duplicate") == true || e.message?.contains("unique") == true) {
                logDebug("Lore already discovered, skipping: ${loreDiscovery.title}")
                Result.success(Unit)
            } else {
                logError("Failed to add lore discovery", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun getLoreDiscoveries(characterId: String): Result<List<LoreDiscovery>> {
        return try {
            logDebug("Fetching lore discoveries for character: $characterId")
            val response = supabaseClient
                .from(TABLE_LORE)
                .select {
                    filter {
                        eq("character_id", characterId)
                    }
                }
                .decodeList<LoreDiscoveryDto>()

            Result.success(response.map { it.toDomainModel() })
        } catch (e: Exception) {
            logError("Failed to fetch lore discoveries", e)
            Result.failure(e)
        }
    }

    override suspend fun getLoreDiscoveriesByCategory(
        characterId: String,
        category: String
    ): Result<List<LoreDiscovery>> {
        return try {
            logDebug("Fetching lore discoveries by category $category for character: $characterId")
            val response = supabaseClient
                .from(TABLE_LORE)
                .select {
                    filter {
                        eq("character_id", characterId)
                        eq("lore_category", category)
                    }
                }
                .decodeList<LoreDiscoveryDto>()

            Result.success(response.map { it.toDomainModel() })
        } catch (e: Exception) {
            logError("Failed to fetch lore discoveries by category", e)
            Result.failure(e)
        }
    }

    override suspend fun hasDiscoveredLore(characterId: String, loreTitle: String): Result<Boolean> {
        return try {
            val response = supabaseClient
                .from(TABLE_LORE)
                .select(Columns.raw("count")) {
                    filter {
                        eq("character_id", characterId)
                        eq("lore_title", loreTitle)
                    }
                }

            // If we got a response, lore has been discovered
            Result.success(true)
        } catch (e: Exception) {
            // Not found or error
            Result.success(false)
        }
    }

    override fun getLoreDiscoveriesFlow(characterId: String): Flow<List<LoreDiscovery>> = flow {
        // For now, just emit the current state
        // TODO: Implement realtime subscriptions when needed
        val result = getLoreDiscoveries(characterId)
        result.getOrNull()?.let { emit(it) }
    }
}
