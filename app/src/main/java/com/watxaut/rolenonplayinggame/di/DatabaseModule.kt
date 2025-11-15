package com.watxaut.rolenonplayinggame.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.watxaut.rolenonplayinggame.data.local.dao.ActivityDao
import com.watxaut.rolenonplayinggame.data.local.dao.CharacterDao
import com.watxaut.rolenonplayinggame.data.local.database.GameDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Migration from version 1 to 2: Added ActivityEntity table
     */
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create activities table (added in v2)
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `activities` (
                    `id` TEXT NOT NULL PRIMARY KEY,
                    `characterId` TEXT NOT NULL,
                    `type` TEXT NOT NULL,
                    `message` TEXT NOT NULL,
                    `timestamp` INTEGER NOT NULL
                )
            """.trimIndent())

            // Create index on characterId for faster queries
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS `index_activities_characterId`
                ON `activities` (`characterId`)
            """.trimIndent())

            // Create index on timestamp for chronological queries
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS `index_activities_timestamp`
                ON `activities` (`timestamp`)
            """.trimIndent())
        }
    }

    /**
     * Migration from version 2 to 3: Added mission tracking fields to characters table
     */
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add mission tracking columns to characters table
            database.execSQL("""
                ALTER TABLE characters ADD COLUMN activePrincipalMissionId TEXT DEFAULT NULL
            """.trimIndent())

            database.execSQL("""
                ALTER TABLE characters ADD COLUMN principalMissionStartedAt INTEGER DEFAULT NULL
            """.trimIndent())

            database.execSQL("""
                ALTER TABLE characters ADD COLUMN principalMissionCompletedCount INTEGER NOT NULL DEFAULT 0
            """.trimIndent())
        }
    }

    @Provides
    @Singleton
    fun provideGameDatabase(
        @ApplicationContext context: Context
    ): GameDatabase {
        return Room.databaseBuilder(
            context,
            GameDatabase::class.java,
            GameDatabase.DATABASE_NAME
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            // Only fall back to destructive migration in debug builds for development
            .apply {
                if (com.watxaut.rolenonplayinggame.BuildConfig.DEBUG) {
                    fallbackToDestructiveMigration()
                }
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideCharacterDao(database: GameDatabase): CharacterDao {
        return database.characterDao()
    }

    @Provides
    @Singleton
    fun provideActivityDao(database: GameDatabase): ActivityDao {
        return database.activityDao()
    }
}
