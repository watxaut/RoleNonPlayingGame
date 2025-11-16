package com.watxaut.rolenonplayinggame.data.repository

import android.util.Log
import com.watxaut.rolenonplayinggame.BuildConfig
import com.watxaut.rolenonplayinggame.data.local.dao.CharacterDao
import com.watxaut.rolenonplayinggame.data.local.entity.CharacterEntity
import com.watxaut.rolenonplayinggame.data.remote.dto.SupabaseCharacterDto
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

/**
 * Implementation of CharacterRepository
 * Handles both local (Room) and remote (Supabase) data sources
 */
class CharacterRepositoryImpl @Inject constructor(
    private val characterDao: CharacterDao,
    private val supabaseClient: SupabaseClient
) : CharacterRepository {

    companion object {
        private const val TAG = "CharacterRepository"

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

    override fun getCharactersByUserId(userId: String): Flow<List<Character>> {
        return characterDao.getCharactersByUserIdFlow(userId)
            .map { entities ->
                entities.map { it.toDomainModel() }
            }
    }

    override suspend fun getCharacterById(characterId: String): Character? {
        return characterDao.getCharacterById(characterId)?.toDomainModel()
    }

    override fun getCharacterByIdFlow(characterId: String): Flow<Character?> {
        return characterDao.getCharacterByIdFlow(characterId)
            .map { it?.toDomainModel() }
    }

    override suspend fun createCharacter(character: Character): Result<Character> {
        return try {
            val entity = CharacterEntity.fromDomainModel(character)

            // Save to local database first
            characterDao.insertCharacter(entity)

            // Sync to Supabase
            try {
                val supabaseData = SupabaseCharacterDto(
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
                    equippedItems = toEquipmentJson(character.equipment),
                    discoveredLocations = toJsonArray(character.discoveredLocations),
                    activeQuests = toJsonArray(character.activeQuests),
                    activePrincipalMissionId = character.activePrincipalMissionId,
                    principalMissionStartedAt = character.principalMissionStartedAt?.toString(),
                    principalMissionCompletedCount = character.principalMissionCompletedCount,
                    createdAt = character.createdAt.toString(),
                    lastActiveAt = character.lastActiveAt.toString()
                )

                supabaseClient.from("characters").insert(supabaseData)
            } catch (e: Exception) {
                logError("Failed to sync character to Supabase, but saved locally", e)
                // Don't fail the entire operation if Supabase sync fails
            }

            Result.success(character)
        } catch (e: Exception) {
            logError("Failed to create character", e)
            Result.failure(e)
        }
    }

    override suspend fun updateCharacter(character: Character): Result<Unit> {
        return try {
            val entity = CharacterEntity.fromDomainModel(character)
            characterDao.updateCharacter(entity)

            // Sync to Supabase
            try {
                val supabaseData = SupabaseCharacterDto(
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
                    equippedItems = toEquipmentJson(character.equipment),
                    discoveredLocations = toJsonArray(character.discoveredLocations),
                    activeQuests = toJsonArray(character.activeQuests),
                    activePrincipalMissionId = character.activePrincipalMissionId,
                    principalMissionStartedAt = character.principalMissionStartedAt?.toString(),
                    principalMissionCompletedCount = character.principalMissionCompletedCount,
                    createdAt = character.createdAt.toString(),
                    lastActiveAt = character.lastActiveAt.toString()
                )

                supabaseClient.from("characters")
                    .update(supabaseData) {
                        filter {
                            eq("id", character.id)
                        }
                    }
            } catch (e: Exception) {
                logError("Failed to sync character update to Supabase, but saved locally", e)
                // Don't fail the entire operation
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCharacter(characterId: String): Result<Unit> {
        return try {
            characterDao.deleteCharacterById(characterId)

            // Delete from Supabase
            try {
                supabaseClient.from("characters")
                    .delete {
                        filter {
                            eq("id", characterId)
                        }
                    }
            } catch (e: Exception) {
                logError("Failed to delete character from Supabase, but deleted locally", e)
                // Don't fail the entire operation
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncCharacter(characterId: String): Result<Unit> {
        // TODO: Implement Supabase sync
        return Result.success(Unit)
    }

    override suspend fun updateCharacterState(
        characterId: String,
        currentHp: Int,
        location: String
    ): Result<Unit> {
        return try {
            characterDao.updateCharacterState(
                characterId = characterId,
                hp = currentHp,
                location = location,
                timestamp = Instant.now().toEpochMilli()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addGold(characterId: String, amount: Long): Result<Unit> {
        return try {
            characterDao.addGold(characterId, amount)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLastActiveTime(characterId: String): Result<Unit> {
        return try {
            characterDao.updateLastActiveTime(
                characterId = characterId,
                timestamp = Instant.now().toEpochMilli()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllCharacters(): Result<List<Character>> {
        return try {
            // Try to fetch from Supabase first for the most up-to-date leaderboard
            try {
                logDebug("Fetching all characters from Supabase for leaderboard")
                val response = supabaseClient.from("characters")
                    .select()
                    .decodeList<com.watxaut.rolenonplayinggame.data.remote.dto.SupabaseCharacterDto>()

                val characters = response.map { it.toDomainModel() }
                logDebug("Fetched ${characters.size} characters from Supabase")
                Result.success(characters)
            } catch (e: Exception) {
                logError("Failed to fetch from Supabase, falling back to local database", e)
                // Fallback to local database
                val localCharacters = characterDao.getAllCharacters().map { it.toDomainModel() }
                Result.success(localCharacters)
            }
        } catch (e: Exception) {
            logError("Failed to fetch all characters", e)
            Result.failure(e)
        }
    }

    /**
     * Convert list to JSON array string for Supabase
     */
    private fun toJsonArray(list: List<String>): String {
        if (list.isEmpty()) return "[]"
        return list.joinToString(",", "[", "]") { "\"$it\"" }
    }

    /**
     * Convert map to JSON object string for Supabase
     */
    private fun toJsonMap(map: Map<String, String>): String {
        if (map.isEmpty()) return "{}"
        return map.entries.joinToString(",", "{", "}") { "\"${it.key}\":\"${it.value}\"" }
    }

    /**
     * Convert equipment loadout to JSON string for Supabase
     */
    private fun toEquipmentJson(equipment: com.watxaut.rolenonplayinggame.domain.model.EquipmentLoadout): String {
        // Serialize equipment as JSON with equipment IDs
        // Example: {"weaponMain":"rusty_sword","armor":"leather_armor"}
        val parts = mutableListOf<String>()

        equipment.weaponMain?.let { parts.add("\"weaponMain\":\"${it.id}\"") }
        equipment.weaponOff?.let { parts.add("\"weaponOff\":\"${it.id}\"") }
        equipment.armor?.let { parts.add("\"armor\":\"${it.id}\"") }
        equipment.gloves?.let { parts.add("\"gloves\":\"${it.id}\"") }
        equipment.head?.let { parts.add("\"head\":\"${it.id}\"") }
        equipment.accessory?.let { parts.add("\"accessory\":\"${it.id}\"") }

        return if (parts.isEmpty()) "{}" else parts.joinToString(",", "{", "}")
    }
}
