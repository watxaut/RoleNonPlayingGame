package com.watxaut.rolenonplayinggame.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for inserting/updating characters in Supabase
 * Uses @Serializable to ensure proper kotlinx.serialization support
 */
@Serializable
data class SupabaseCharacterDto(
    @SerialName("id")
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("name")
    val name: String,

    @SerialName("level")
    val level: Int,

    @SerialName("experience")
    val experience: Long,

    @SerialName("strength")
    val strength: Int,

    @SerialName("intelligence")
    val intelligence: Int,

    @SerialName("agility")
    val agility: Int,

    @SerialName("luck")
    val luck: Int,

    @SerialName("charisma")
    val charisma: Int,

    @SerialName("vitality")
    val vitality: Int,

    @SerialName("current_hp")
    val currentHp: Int,

    @SerialName("max_hp")
    val maxHp: Int,

    @SerialName("current_location")
    val currentLocation: String,

    @SerialName("personality_courage")
    val personalityCourage: Float,

    @SerialName("personality_greed")
    val personalityGreed: Float,

    @SerialName("personality_curiosity")
    val personalityCuriosity: Float,

    @SerialName("personality_aggression")
    val personalityAggression: Float,

    @SerialName("personality_social")
    val personalitySocial: Float,

    @SerialName("personality_impulsive")
    val personalityImpulsive: Float,

    @SerialName("job_class")
    val jobClass: String,

    @SerialName("gold")
    val gold: Long,

    @SerialName("inventory")
    val inventory: String,

    @SerialName("equipped_items")
    val equippedItems: String,

    @SerialName("discovered_locations")
    val discoveredLocations: String,

    @SerialName("active_quests")
    val activeQuests: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("last_active_at")
    val lastActiveAt: String
)
