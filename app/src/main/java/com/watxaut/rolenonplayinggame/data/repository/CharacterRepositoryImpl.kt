package com.watxaut.rolenonplayinggame.data.repository

import com.watxaut.rolenonplayinggame.data.local.dao.CharacterDao
import com.watxaut.rolenonplayinggame.data.local.entity.CharacterEntity
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

/**
 * Implementation of CharacterRepository
 * Handles both local (Room) and remote (Supabase) data sources
 */
class CharacterRepositoryImpl @Inject constructor(
    private val characterDao: CharacterDao
    // TODO: Add Supabase client when implementing remote sync
) : CharacterRepository {

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
            characterDao.insertCharacter(entity)

            // TODO: Sync to Supabase when remote sync is implemented

            Result.success(character)
        } catch (e: Exception) {
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
