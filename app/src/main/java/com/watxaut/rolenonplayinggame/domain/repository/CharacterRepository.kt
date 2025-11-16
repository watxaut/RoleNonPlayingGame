package com.watxaut.rolenonplayinggame.domain.repository

import com.watxaut.rolenonplayinggame.domain.model.Character
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for character data operations
 */
interface CharacterRepository {

    /**
     * Get all characters for a user as a Flow
     */
    fun getCharactersByUserId(userId: String): Flow<List<Character>>

    /**
     * Get a specific character by ID
     */
    suspend fun getCharacterById(characterId: String): Character?

    /**
     * Get a character by ID as a Flow
     */
    fun getCharacterByIdFlow(characterId: String): Flow<Character?>

    /**
     * Create a new character
     */
    suspend fun createCharacter(character: Character): Result<Character>

    /**
     * Update an existing character
     */
    suspend fun updateCharacter(character: Character): Result<Unit>

    /**
     * Delete a character
     */
    suspend fun deleteCharacter(characterId: String): Result<Unit>

    /**
     * Sync character data with remote server
     */
    suspend fun syncCharacter(characterId: String): Result<Unit>

    /**
     * Update character's current state (HP, location)
     */
    suspend fun updateCharacterState(characterId: String, currentHp: Int, location: String): Result<Unit>

    /**
     * Add gold to character
     */
    suspend fun addGold(characterId: String, amount: Long): Result<Unit>

    /**
     * Update last active timestamp
     */
    suspend fun updateLastActiveTime(characterId: String): Result<Unit>

    /**
     * Get all characters from all users (for leaderboard)
     */
    suspend fun getAllCharacters(): Result<List<Character>>
}
