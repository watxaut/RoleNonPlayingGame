package com.watxaut.rolenonplayinggame.data.repository

import android.util.Log
import com.watxaut.rolenonplayinggame.data.local.dao.CharacterDao
import com.watxaut.rolenonplayinggame.data.local.entity.CharacterEntity
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
                val supabaseData = mapOf(
                    "id" to character.id,
                    "user_id" to character.userId, // This should be a valid UUID
                    "name" to character.name,
                    "level" to character.level,
                    "experience" to character.experience,
                    "strength" to character.strength,
                    "intelligence" to character.intelligence,
                    "agility" to character.agility,
                    "luck" to character.luck,
                    "charisma" to character.charisma,
                    "vitality" to character.vitality,
                    "current_hp" to character.currentHp,
                    "max_hp" to character.maxHp,
                    "current_location" to character.currentLocation,
                    "personality_courage" to character.personalityTraits.courage,
                    "personality_greed" to character.personalityTraits.greed,
                    "personality_curiosity" to character.personalityTraits.curiosity,
                    "personality_aggression" to character.personalityTraits.aggression,
                    "personality_social" to character.personalityTraits.social,
                    "personality_impulsive" to character.personalityTraits.impulsive,
                    "job_class" to character.jobClass.name,
                    "gold" to character.gold,
                    "inventory" to character.inventory,
                    "equipped_items" to character.equippedItems,
                    "discovered_locations" to character.discoveredLocations,
                    "active_quests" to character.activeQuests,
                    "created_at" to character.createdAt.toString(),
                    "last_active_at" to character.lastActiveAt.toString()
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
}
