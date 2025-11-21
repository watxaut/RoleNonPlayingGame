package com.watxaut.rolenonplayinggame.di

import android.content.Context
import com.watxaut.rolenonplayinggame.BuildConfig
import com.watxaut.rolenonplayinggame.data.remote.api.SupabaseApi
import com.watxaut.rolenonplayinggame.data.remote.api.SupabaseConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * Hilt module for network-related dependencies (Supabase)
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(
        @ApplicationContext context: Context
    ): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Auth) {
                // CRITICAL: Enable session persistence to SharedPreferences
                // Without this, sessions are lost when app backgrounds/restarts
                autoSaveToStorage = true
                autoLoadFromStorage = true
                // Explicitly enable session auto-refresh to keep user logged in
                alwaysAutoRefresh = true
            }
            install(Postgrest)
            install(Realtime)
        }
    }

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = BuildConfig.DEBUG
                    isLenient = true
                    // Keep ignoreUnknownKeys for forward compatibility with API changes
                    // But log unknown keys in debug builds for awareness
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                logger = Logger.DEFAULT
                // Only log in debug builds to prevent sensitive data exposure
                level = if (BuildConfig.DEBUG) LogLevel.INFO else LogLevel.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideSupabaseConfig(): SupabaseConfig {
        return SupabaseConfig(
            url = BuildConfig.SUPABASE_URL,
            anonKey = BuildConfig.SUPABASE_KEY
        )
    }

    @Provides
    @Singleton
    fun provideSupabaseApi(
        httpClient: HttpClient,
        config: SupabaseConfig,
        supabaseClient: SupabaseClient
    ): SupabaseApi {
        return SupabaseApi(httpClient, config, supabaseClient)
    }
}
