package com.watxaut.rolenonplayinggame.di

import com.watxaut.rolenonplayinggame.data.repository.ActivityRepositoryImpl
import com.watxaut.rolenonplayinggame.data.repository.AuthRepositoryImpl
import com.watxaut.rolenonplayinggame.data.repository.CharacterRepositoryImpl
import com.watxaut.rolenonplayinggame.data.repository.MissionProgressRepositoryImpl
import com.watxaut.rolenonplayinggame.data.repository.SocialRepositoryImpl
import com.watxaut.rolenonplayinggame.domain.repository.ActivityRepository
import com.watxaut.rolenonplayinggame.domain.repository.AuthRepository
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import com.watxaut.rolenonplayinggame.domain.repository.MissionProgressRepository
import com.watxaut.rolenonplayinggame.domain.repository.SocialRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for repository dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        impl: CharacterRepositoryImpl
    ): CharacterRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(
        impl: ActivityRepositoryImpl
    ): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindMissionProgressRepository(
        impl: MissionProgressRepositoryImpl
    ): MissionProgressRepository

    @Binds
    @Singleton
    abstract fun bindSocialRepository(
        impl: SocialRepositoryImpl
    ): SocialRepository
}
