package com.watxaut.rolenonplayinggame.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.watxaut.rolenonplayinggame.domain.model.Activity
import com.watxaut.rolenonplayinggame.domain.model.ActivityRewards
import com.watxaut.rolenonplayinggame.domain.model.ActivityType
import java.time.Instant

/**
 * Room entity for Activity Log persistence.
 *
 * Per TECHNICAL_IMPLEMENTATION_DOCUMENT.md:
 * - Store last 500 activities per character
 * - Include timestamps for ordering
 * - Support major event filtering
 */
@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val characterId: String,
    val timestamp: Long, // Unix timestamp (seconds since epoch)
    val type: String, // ActivityType as string
    val description: String,
    val isMajorEvent: Boolean,

    // Rewards (nullable fields)
    val rewardExperience: Int?,
    val rewardGold: Int?,
    val rewardItems: String?, // JSON string of item list

    // Metadata (JSON string)
    val metadata: String // Map<String, String> as JSON
) {
    /**
     * Convert entity to domain model.
     */
    fun toDomain(): Activity {
        val rewards = if (rewardExperience != null || rewardGold != null) {
            ActivityRewards(
                experience = rewardExperience ?: 0,
                gold = rewardGold ?: 0,
                items = rewardItems?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
            )
        } else null

        // Parse metadata JSON (simple key=value format for now)
        val metadataMap = if (metadata.isNotBlank()) {
            metadata.split(";")
                .filter { it.contains("=") }
                .associate {
                    val (key, value) = it.split("=", limit = 2)
                    key to value
                }
        } else {
            emptyMap()
        }

        return Activity(
            id = id,
            characterId = characterId,
            timestamp = Instant.ofEpochSecond(timestamp),
            type = ActivityType.valueOf(type),
            description = description,
            isMajorEvent = isMajorEvent,
            rewards = rewards,
            metadata = metadataMap
        )
    }

    companion object {
        /**
         * Convert domain model to entity.
         */
        fun fromDomain(activity: Activity): ActivityEntity {
            val rewardItems = activity.rewards?.items?.joinToString(",") ?: ""

            // Serialize metadata as key=value;key=value
            val metadataString = activity.metadata.entries.joinToString(";") { (key, value) ->
                "$key=$value"
            }

            return ActivityEntity(
                id = activity.id,
                characterId = activity.characterId,
                timestamp = activity.timestamp.epochSecond,
                type = activity.type.name,
                description = activity.description,
                isMajorEvent = activity.isMajorEvent,
                rewardExperience = activity.rewards?.experience,
                rewardGold = activity.rewards?.gold,
                rewardItems = rewardItems.ifBlank { null },
                metadata = metadataString
            )
        }
    }
}
