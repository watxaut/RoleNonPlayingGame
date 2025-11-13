package com.watxaut.rolenonplayinggame.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.watxaut.rolenonplayinggame.data.local.dao.ActivityDao
import com.watxaut.rolenonplayinggame.data.local.dao.CharacterDao
import com.watxaut.rolenonplayinggame.data.local.entity.ActivityEntity
import com.watxaut.rolenonplayinggame.data.local.entity.CharacterEntity

/**
 * Main Room database for the game
 *
 * Version history:
 * - v1: Initial database with CharacterEntity
 * - v2: Added ActivityEntity for activity logging (Week 3)
 */
@Database(
    entities = [CharacterEntity::class, ActivityEntity::class],
    version = 2,
    exportSchema = false
)
abstract class GameDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun activityDao(): ActivityDao

    companion object {
        const val DATABASE_NAME = "role_non_playing_game.db"
    }
}
