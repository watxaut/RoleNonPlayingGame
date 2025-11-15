package com.watxaut.rolenonplayinggame.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.model.EquipmentLoadout
import com.watxaut.rolenonplayinggame.domain.model.JobClass
import com.watxaut.rolenonplayinggame.domain.model.PersonalityTraits
import java.time.Instant

/**
 * Room entity for storing character data locally
 */
@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val level: Int,
    val experience: Long,

    // Stats
    val strength: Int,
    val intelligence: Int,
    val agility: Int,
    val luck: Int,
    val charisma: Int,
    val vitality: Int,

    // State
    val currentHp: Int,
    val maxHp: Int,
    val currentLocation: String,

    // Personality traits (stored as separate columns for easy querying)
    val personalityCourage: Float,
    val personalityGreed: Float,
    val personalityCuriosity: Float,
    val personalityAggression: Float,
    val personalitySocial: Float,
    val personalityImpulsive: Float,

    // Job class
    val jobClass: String,

    // Resources
    val gold: Long,

    // JSON stored fields (Room will store these as strings)
    val inventory: String,  // JSON array
    val equipment: String,  // JSON object for EquipmentLoadout
    val discoveredLocations: String,  // JSON array
    val activeQuests: String,  // JSON array

    // Timestamps
    val createdAt: Long,  // Unix timestamp
    val lastActiveAt: Long  // Unix timestamp
) {
    /**
     * Convert entity to domain model
     */
    fun toDomainModel(): Character {
        return Character(
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
            personalityTraits = PersonalityTraits(
                courage = personalityCourage,
                greed = personalityGreed,
                curiosity = personalityCuriosity,
                aggression = personalityAggression,
                social = personalitySocial,
                impulsive = personalityImpulsive
            ),
            jobClass = JobClass.valueOf(jobClass),
            gold = gold,
            inventory = parseJsonArray(inventory),
            equipment = parseEquipmentLoadout(equipment),
            discoveredLocations = parseJsonArray(discoveredLocations),
            activeQuests = parseJsonArray(activeQuests),
            createdAt = Instant.ofEpochMilli(createdAt),
            lastActiveAt = Instant.ofEpochMilli(lastActiveAt)
        )
    }

    private fun parseJsonArray(json: String): List<String> {
        if (json.isEmpty() || json == "[]") return emptyList()
        // Simple parsing for now - in production would use kotlinx.serialization
        return json.trim('[', ']')
            .split(",")
            .map { it.trim().trim('"') }
            .filter { it.isNotEmpty() }
    }

    private fun parseJsonMap(json: String): Map<String, String> {
        if (json.isEmpty() || json == "{}") return emptyMap()
        // Simple parsing for now
        val result = mutableMapOf<String, String>()
        json.trim('{', '}')
            .split(",")
            .forEach { pair ->
                val parts = pair.split(":")
                if (parts.size == 2) {
                    val key = parts[0].trim().trim('"')
                    val value = parts[1].trim().trim('"')
                    result[key] = value
                }
            }
        return result
    }

    private fun parseEquipmentLoadout(json: String): EquipmentLoadout {
        if (json.isEmpty() || json == "{}") return EquipmentLoadout()
        // For now, return empty loadout. Equipment will be handled separately
        // TODO: Implement proper JSON parsing when equipment system is fully integrated
        return EquipmentLoadout()
    }

    companion object {
        /**
         * Convert domain model to entity
         */
        fun fromDomainModel(character: Character): CharacterEntity {
            return CharacterEntity(
                id = character.id,
                userId = character.userId,
                name = character.name,
                level = character.level,
                experience = character.experience,
                strength = character.strength,
                intelligence = character.intelligence,
                agility = character.agility,
                luck = character.luck,
                charisma = character.charisma,
                vitality = character.vitality,
                currentHp = character.currentHp,
                maxHp = character.maxHp,
                currentLocation = character.currentLocation,
                personalityCourage = character.personalityTraits.courage,
                personalityGreed = character.personalityTraits.greed,
                personalityCuriosity = character.personalityTraits.curiosity,
                personalityAggression = character.personalityTraits.aggression,
                personalitySocial = character.personalityTraits.social,
                personalityImpulsive = character.personalityTraits.impulsive,
                jobClass = character.jobClass.name,
                gold = character.gold,
                inventory = toJsonArray(character.inventory),
                equipment = toEquipmentJson(character.equipment),
                discoveredLocations = toJsonArray(character.discoveredLocations),
                activeQuests = toJsonArray(character.activeQuests),
                createdAt = character.createdAt.toEpochMilli(),
                lastActiveAt = character.lastActiveAt.toEpochMilli()
            )
        }

        private fun toJsonArray(list: List<String>): String {
            if (list.isEmpty()) return "[]"
            return list.joinToString(",", "[", "]") { "\"$it\"" }
        }

        private fun toJsonMap(map: Map<String, String>): String {
            if (map.isEmpty()) return "{}"
            return map.entries.joinToString(",", "{", "}") { "\"${it.key}\":\"${it.value}\"" }
        }

        private fun toEquipmentJson(equipment: EquipmentLoadout): String {
            // For now, return empty JSON. Equipment will be handled separately
            // TODO: Implement proper JSON serialization when equipment system is fully integrated
            return "{}"
        }
    }
}
