package com.watxaut.rolenonplayinggame.data.repository

import android.util.Log
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
            Log.d(TAG, "Character saved to local database: ${character.name}")

            // Sync to Supabase (with proper authentication handling)
            try {
                // Use service role key to bypass RLS for now (development mode)
                // In production, use proper authentication
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
                    equippedItems = toJsonMap(character.equippedItems),
                    discoveredLocations = toJsonArray(character.discoveredLocations),
                    activeQuests = toJsonArray(character.activeQuests),
                    createdAt = character.createdAt.toString(),
                    lastActiveAt = character.lastActiveAt.toString()
                )

                Log.d(TAG, "Attempting to insert character to Supabase: ${character.name} with user_id: ${character.userId}")
                val response = supabaseClient.from("characters").insert(supabaseData)
                Log.d(TAG, "Supabase insert response: $response")
                Log.d(TAG, "Character synced to Supabase successfully: ${character.name}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync character to Supabase, but saved locally. Error: ${e.message}", e)
                e.printStackTrace()
                // Don't fail the entire operation if Supabase sync fails
                // Character is still saved locally
            }

            Result.success(character)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create character", e)
            Result.failure(e)
        }
    }

    override suspend fun updateCharacter(character: Character): Result<Unit> {
        return try {
            val entity = CharacterEntity.fromDomainModel(character)
            characterDao.updateCharacter(entity)

            // TODO: Sync to Supabase when remote sync is implemented

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCharacter(characterId: String): Result<Unit> {
        return try {
            characterDao.deleteCharacterById(characterId)

            // TODO: Delete from Supabase when remote sync is implemented

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
}
