package com.watxaut.rolenonplayinggame.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request DTO for offline simulation
 */
@Serializable
data class OfflineSimulationRequest(
    @SerialName("characterId")
    val characterId: String
)

/**
 * Response DTO for offline simulation
 */
@Serializable
data class OfflineSimulationResponse(
    @SerialName("message")
    val message: String,

    @SerialName("gameHoursSimulated")
    val gameHoursSimulated: Int,

    @SerialName("realHoursOffline")
    val realHoursOffline: Double,

    @SerialName("activities")
    val activities: List<ActivityDto>,

    @SerialName("summary")
    val summary: SimulationSummaryDto,

    @SerialName("characterState")
    val characterState: CharacterStateDto,

    @SerialName("characterName")
    val characterName: String? = null,

    @SerialName("missionProgress")
    val missionProgress: MissionProgressDto? = null
)

/**
 * Mission progress from simulation
 */
@Serializable
data class MissionProgressDto(
    @SerialName("principalMissionSteps")
    val principalMissionSteps: Int = 0,

    @SerialName("secondaryMissionsDiscovered")
    val secondaryMissionsDiscovered: Int = 0,

    @SerialName("loreDiscovered")
    val loreDiscovered: Int = 0
)

/**
 * Activity DTO from server
 */
@Serializable
data class ActivityDto(
    @SerialName("timestamp")
    val timestamp: String,

    @SerialName("activity_type")
    val activityType: String,

    @SerialName("description")
    val description: String,

    @SerialName("rewards")
    val rewards: RewardsDto? = null,

    @SerialName("metadata")
    val metadata: Map<String, String>? = null,

    @SerialName("is_major_event")
    val isMajorEvent: Boolean
)

/**
 * Rewards DTO
 */
@Serializable
data class RewardsDto(
    @SerialName("xp")
    val xp: Int? = null,

    @SerialName("gold")
    val gold: Int? = null,

    @SerialName("items")
    val items: List<Map<String, String>>? = null
)

/**
 * Simulation summary DTO
 */
@Serializable
data class SimulationSummaryDto(
    @SerialName("totalCombats")
    val totalCombats: Int,

    @SerialName("combatsWon")
    val combatsWon: Int,

    @SerialName("combatsLost")
    val combatsLost: Int,

    @SerialName("totalXpGained")
    val totalXpGained: Int,

    @SerialName("totalGoldGained")
    val totalGoldGained: Int,

    @SerialName("levelsGained")
    val levelsGained: Int,

    @SerialName("deaths")
    val deaths: Int,

    @SerialName("locationsDiscovered")
    val locationsDiscovered: Int,

    @SerialName("itemsFound")
    val itemsFound: Int,

    @SerialName("majorEvents")
    val majorEvents: List<String>
)

/**
 * Character state DTO from simulation
 */
@Serializable
data class CharacterStateDto(
    @SerialName("level")
    val level: Int,

    @SerialName("experience")
    val experience: Long,

    @SerialName("gold")
    val gold: Long,

    @SerialName("current_hp")
    val currentHp: Int,

    @SerialName("max_hp")
    val maxHp: Int
)
