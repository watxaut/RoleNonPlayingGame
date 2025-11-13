package com.watxaut.rolenonplayinggame.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.watxaut.rolenonplayinggame.data.local.dao.CharacterDao
import com.watxaut.rolenonplayinggame.data.local.entity.CharacterEntity

/**
 * Main Room database for the game
 */
@Database(
    entities = [CharacterEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GameDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao

    companion object {
        const val DATABASE_NAME = "role_non_playing_game.db"
    }
}
