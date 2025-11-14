package com.watxaut.rolenonplayinggame.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.watxaut.rolenonplayinggame.data.local.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Character entities
 */
@Dao
interface CharacterDao {

    @Query("SELECT * FROM characters WHERE userId = :userId")
    fun getCharactersByUserIdFlow(userId: String): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE userId = :userId")
    suspend fun getCharactersByUserId(userId: String): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE id = :characterId LIMIT 1")
    suspend fun getCharacterById(characterId: String): CharacterEntity?

    @Query("SELECT * FROM characters WHERE id = :characterId LIMIT 1")
    fun getCharacterByIdFlow(characterId: String): Flow<CharacterEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Delete
    suspend fun deleteCharacter(character: CharacterEntity)

    @Query("DELETE FROM characters WHERE id = :characterId")
    suspend fun deleteCharacterById(characterId: String)

    @Query("DELETE FROM characters WHERE userId = :userId")
    suspend fun deleteAllCharactersByUserId(userId: String)

    @Query("SELECT COUNT(*) FROM characters WHERE userId = :userId")
    suspend fun getCharacterCountByUserId(userId: String): Int

    @Query("UPDATE characters SET currentHp = :hp, currentLocation = :location, lastActiveAt = :timestamp WHERE id = :characterId")
    suspend fun updateCharacterState(characterId: String, hp: Int, location: String, timestamp: Long)

    @Query("UPDATE characters SET level = :level, experience = :experience, strength = :str, intelligence = :intel, agility = :agi, luck = :lck, charisma = :cha, vitality = :vit, maxHp = :maxHp WHERE id = :characterId")
    suspend fun updateCharacterStats(
        characterId: String,
        level: Int,
        experience: Long,
        str: Int,
        intel: Int,
        agi: Int,
        lck: Int,
        cha: Int,
        vit: Int,
        maxHp: Int
    )

    @Query("UPDATE characters SET gold = gold + :amount WHERE id = :characterId")
    suspend fun addGold(characterId: String, amount: Long)

    @Query("UPDATE characters SET lastActiveAt = :timestamp WHERE id = :characterId")
    suspend fun updateLastActiveTime(characterId: String, timestamp: Long)
}
