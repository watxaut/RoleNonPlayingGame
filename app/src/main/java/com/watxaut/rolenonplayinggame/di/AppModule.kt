package com.watxaut.rolenonplayinggame.di

import com.watxaut.rolenonplayinggame.core.combat.CombatSystem
import com.watxaut.rolenonplayinggame.core.dice.DiceRoller
import com.watxaut.rolenonplayinggame.core.messages.MessageProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Hilt module for application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDiceRoller(): DiceRoller {
        return DiceRoller()
    }

    @Provides
    @Singleton
    fun provideMessageProvider(): MessageProvider {
        return MessageProvider()
    }

    @Provides
    @Singleton
    fun provideCombatSystem(
        diceRoller: DiceRoller,
        messageProvider: MessageProvider
    ): CombatSystem {
        return CombatSystem(diceRoller, messageProvider)
    }

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher
