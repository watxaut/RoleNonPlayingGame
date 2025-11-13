package com.watxaut.rolenonplayinggame.domain.repository

import com.watxaut.rolenonplayinggame.domain.model.Activity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Activity Log operations.
 *
 * Per TECHNICAL_IMPLEMENTATION_DOCUMENT.md:
 * - Real-time action logging (last 500 entries)
 * - Offline summary generation
 * - Event categorization and filtering
 * - Timestamp management
 *
 * Clean Architecture: Domain layer defines interface, Data layer implements it.
 */
interface ActivityRepository {

    /**
     * Get recent activities for a character.
     * Returns a Flow for reactive updates as new activities are logged.
     *
     * @param characterId The character's unique ID
     * @param limit Maximum number of activities to return (default 500)
     * @return Flow of activity lists, ordered by timestamp descending (newest first)
     */
    fun getActivitiesForCharacter(characterId: String, limit: Int = 500): Flow<List<Activity>>

    /**
     * Get major events only (level ups, deaths, boss defeats, etc.)
     * Useful for "While you were away" summary screen.
     *
     * @param characterId The character's unique ID
     * @param limit Maximum number of activities to return
     * @return Flow of major event activities
     */
    fun getMajorEventsForCharacter(characterId: String, limit: Int = 50): Flow<List<Activity>>

    /**
     * Log a new activity for a character.
     * This is the primary method called by the game engine as actions occur.
     *
     * @param activity The activity to log
     * @return Result indicating success or failure
     */
    suspend fun logActivity(activity: Activity): Result<Unit>

    /**
     * Log multiple activities in a batch.
     * Used for offline simulation where many activities are generated at once.
     *
     * @param activities List of activities to log
     * @return Result indicating success or failure
     */
    suspend fun logActivities(activities: List<Activity>): Result<Unit>

    /**
     * Delete old activities to maintain the 500-entry limit.
     * Called periodically to prevent database bloat.
     *
     * @param characterId The character's unique ID
     * @param keepCount Number of most recent activities to keep (default 500)
     * @return Result indicating success or failure
     */
    suspend fun pruneOldActivities(characterId: String, keepCount: Int = 500): Result<Unit>

    /**
     * Get the most recent activity for a character.
     * Useful for determining time since last action.
     *
     * @param characterId The character's unique ID
     * @return The most recent activity, or null if none exist
     */
    suspend fun getLatestActivity(characterId: String): Activity?

    /**
     * Delete all activities for a character.
     * Used when a character is deleted.
     *
     * @param characterId The character's unique ID
     * @return Result indicating success or failure
     */
    suspend fun deleteActivitiesForCharacter(characterId: String): Result<Unit>
}
