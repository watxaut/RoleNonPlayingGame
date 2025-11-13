package com.watxaut.rolenonplayinggame.di

import android.content.Context
import androidx.room.Room
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
            .fallbackToDestructiveMigration()
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
