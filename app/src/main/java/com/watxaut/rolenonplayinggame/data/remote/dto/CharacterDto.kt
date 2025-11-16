package com.watxaut.rolenonplayinggame.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for inserting/updating characters in Supabase
 * Uses @Serializable to ensure proper kotlinx.serialization support
 */
@Serializable
data class SupabaseCharacterDto(
    @SerialName("id")
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("name")
    val name: String,

    @SerialName("level")
    val level: Int,

    @SerialName("experience")
    val experience: Long,

    @SerialName("strength")
    val strength: Int,

    @SerialName("intelligence")
    val intelligence: Int,

    @SerialName("agility")
    val agility: Int,

    @SerialName("luck")
    val luck: Int,

    @SerialName("charisma")
    val charisma: Int,

    @SerialName("vitality")
    val vitality: Int,

    @SerialName("current_hp")
    val currentHp: Int,

    @SerialName("max_hp")
    val maxHp: Int,

    @SerialName("current_location")
    val currentLocation: String,

    @SerialName("personality_courage")
    val personalityCourage: Float,

    @SerialName("personality_greed")
    val personalityGreed: Float,

    @SerialName("personality_curiosity")
    val personalityCuriosity: Float,

    @SerialName("personality_aggression")
    val personalityAggression: Float,

    @SerialName("personality_social")
    val personalitySocial: Float,

    @SerialName("personality_impulsive")
    val personalityImpulsive: Float,

    @SerialName("job_class")
    val jobClass: String,

    @SerialName("gold")
    val gold: Long,

    @SerialName("inventory")
    val inventory: String,

    @SerialName("equipped_items")
    val equippedItems: String,

    @SerialName("discovered_locations")
    val discoveredLocations: String,

    @SerialName("active_quests")
    val activeQuests: String,

    @SerialName("active_principal_mission_id")
    val activePrincipalMissionId: String? = null,

    @SerialName("principal_mission_started_at")
    val principalMissionStartedAt: String? = null,

    @SerialName("principal_mission_completed_count")
    val principalMissionCompletedCount: Int = 0,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("last_active_at")
    val lastActiveAt: String
) {
    /**
     * Convert DTO to domain model
     */
    fun toDomainModel(): com.watxaut.rolenonplayinggame.domain.model.Character {
        return com.watxaut.rolenonplayinggame.domain.model.Character(
            id = id,
            userId = userId,
            name = name,
            level = level,
            experience = experience,
            strength = strength,
            intelligence = intelligence,
            agility = agility,
            luck = luck,
            charisma = charisma,
            vitality = vitality,
            currentHp = currentHp,
            maxHp = maxHp,
            currentLocation = currentLocation,
            personalityTraits = com.watxaut.rolenonplayinggame.domain.model.PersonalityTraits(
                courage = personalityCourage,
                greed = personalityGreed,
                curiosity = personalityCuriosity,
                aggression = personalityAggression,
                social = personalitySocial,
                impulsive = personalityImpulsive
            ),
            jobClass = com.watxaut.rolenonplayinggame.domain.model.JobClass.valueOf(jobClass),
            gold = gold,
            inventory = parseJsonArray(inventory),
            equipment = parseEquipmentLoadout(equippedItems),
            discoveredLocations = parseJsonArray(discoveredLocations),
            activeQuests = parseJsonArray(activeQuests),
            activePrincipalMissionId = activePrincipalMissionId,
            principalMissionStartedAt = principalMissionStartedAt?.let { java.time.Instant.parse(it) },
            principalMissionCompletedCount = principalMissionCompletedCount,
            createdAt = java.time.Instant.parse(createdAt),
            lastActiveAt = java.time.Instant.parse(lastActiveAt)
        )
    }

    private fun parseJsonArray(json: String): List<String> {
        if (json.isEmpty() || json == "[]") return emptyList()
        return json.trim('[', ']')
            .split(",")
            .map { it.trim().trim('"') }
            .filter { it.isNotEmpty() }
    }

    private fun parseEquipmentLoadout(json: String): com.watxaut.rolenonplayinggame.domain.model.EquipmentLoadout {
        if (json.isEmpty() || json == "{}") return com.watxaut.rolenonplayinggame.domain.model.EquipmentLoadout()

        val equipmentIds = mutableMapOf<String, String>()
        json.trim('{', '}')
            .split(",")
            .forEach { pair ->
                val parts = pair.split(":")
                if (parts.size == 2) {
                    val key = parts[0].trim().trim('"')
                    val value = parts[1].trim().trim('"')
                    if (value.isNotEmpty() && value != "null") {
                        equipmentIds[key] = value
                    }
                }
            }

        return com.watxaut.rolenonplayinggame.domain.model.EquipmentLoadout(
            weaponMain = equipmentIds["weaponMain"]?.let {
                com.watxaut.rolenonplayinggame.data.EquipmentDatabase.getEquipmentById(it)
            },
            weaponOff = equipmentIds["weaponOff"]?.let {
                com.watxaut.rolenonplayinggame.data.EquipmentDatabase.getEquipmentById(it)
            },
            armor = equipmentIds["armor"]?.let {
                com.watxaut.rolenonplayinggame.data.EquipmentDatabase.getEquipmentById(it)
            },
            gloves = equipmentIds["gloves"]?.let {
                com.watxaut.rolenonplayinggame.data.EquipmentDatabase.getEquipmentById(it)
            },
            head = equipmentIds["head"]?.let {
                com.watxaut.rolenonplayinggame.data.EquipmentDatabase.getEquipmentById(it)
            },
            accessory = equipmentIds["accessory"]?.let {
                com.watxaut.rolenonplayinggame.data.EquipmentDatabase.getEquipmentById(it)
            }
        )
    }
}
