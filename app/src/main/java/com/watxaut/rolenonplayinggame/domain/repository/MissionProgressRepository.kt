package com.watxaut.rolenonplayinggame.domain.repository

import com.watxaut.rolenonplayinggame.domain.model.LoreDiscovery
import com.watxaut.rolenonplayinggame.domain.model.PrincipalMissionProgress
import com.watxaut.rolenonplayinggame.domain.model.SecondaryMissionProgress
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing mission progress and lore discoveries in Supabase.
 * Handles principal missions, secondary missions, and lore tracking.
 */
interface MissionProgressRepository {

    // ============================================================
    // PRINCIPAL MISSION PROGRESS
    // ============================================================

    /**
     * Get the active principal mission for a character
     */
    suspend fun getActivePrincipalMission(characterId: String): Result<PrincipalMissionProgress?>

    /**
     * Assign a new principal mission to a character
     */
    suspend fun assignPrincipalMission(
        characterId: String,
        missionId: String
    ): Result<Unit>

    /**
     * Mark a mission step as completed
     */
    suspend fun completeStory(
        characterId: String,
        missionId: String,
        stepId: String
    ): Result<Unit>

    /**
     * Mark that the boss has been encountered
     */
    suspend fun markBossEncountered(
        characterId: String,
        missionId: String
    ): Result<Unit>

    /**
     * Mark that the boss has been defeated (completes the mission)
     */
    suspend fun markBossDefeated(
        characterId: String,
        missionId: String
    ): Result<Unit>

    /**
     * Get all completed principal missions for a character
     */
    suspend fun getCompletedPrincipalMissions(characterId: String): Result<List<PrincipalMissionProgress>>

    /**
     * Get principal mission progress as a Flow for real-time updates
     */
    fun getPrincipalMissionProgressFlow(characterId: String): Flow<PrincipalMissionProgress?>

    // ============================================================
    // SECONDARY MISSION PROGRESS
    // ============================================================

    /**
     * Get all active secondary missions for a character
     */
    suspend fun getActiveSecondaryMissions(characterId: String): Result<List<SecondaryMissionProgress>>

    /**
     * Discover a new secondary mission
     */
    suspend fun discoverSecondaryMission(
        characterId: String,
        missionId: String
    ): Result<Unit>

    /**
     * Update the progress text for a secondary mission
     */
    suspend fun updateSecondaryMissionProgress(
        characterId: String,
        missionId: String,
        progressText: String
    ): Result<Unit>

    /**
     * Complete a secondary mission
     */
    suspend fun completeSecondaryMission(
        characterId: String,
        missionId: String,
        rewardExperience: Long,
        rewardGold: Int,
        rewardEquipment: String?
    ): Result<Unit>

    /**
     * Get completed secondary missions for a character
     */
    suspend fun getCompletedSecondaryMissions(characterId: String): Result<List<SecondaryMissionProgress>>

    /**
     * Get secondary mission progress as a Flow for real-time updates
     */
    fun getSecondaryMissionProgressFlow(characterId: String): Flow<List<SecondaryMissionProgress>>

    // ============================================================
    // LORE DISCOVERIES
    // ============================================================

    /**
     * Add a new lore discovery for a character
     */
    suspend fun addLoreDiscovery(characterId: String, loreDiscovery: LoreDiscovery): Result<Unit>

    /**
     * Get all lore discoveries for a character
     */
    suspend fun getLoreDiscoveries(characterId: String): Result<List<LoreDiscovery>>

    /**
     * Get lore discoveries by category
     */
    suspend fun getLoreDiscoveriesByCategory(
        characterId: String,
        category: String
    ): Result<List<LoreDiscovery>>

    /**
     * Check if a specific lore entry has been discovered
     */
    suspend fun hasDiscoveredLore(characterId: String, loreTitle: String): Result<Boolean>

    /**
     * Get lore discoveries as a Flow for real-time updates
     */
    fun getLoreDiscoveriesFlow(characterId: String): Flow<List<LoreDiscovery>>
}
