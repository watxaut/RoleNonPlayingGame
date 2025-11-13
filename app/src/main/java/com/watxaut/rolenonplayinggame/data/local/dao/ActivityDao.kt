package com.watxaut.rolenonplayinggame.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.watxaut.rolenonplayinggame.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Activity Log operations.
 *
 * Per TECHNICAL_IMPLEMENTATION_DOCUMENT.md:
 * - Query last 500 activities per character
 * - Support major event filtering
 * - Efficient ordering by timestamp
 */
@Dao
interface ActivityDao {

    /**
     * Get all activities for a character, ordered by most recent first.
     * Limited to prevent performance issues.
     *
     * @param characterId The character's ID
     * @param limit Maximum number of activities to return
     * @return Flow of activity list
     */
    @Query("""
        SELECT * FROM activities
        WHERE characterId = :characterId
        ORDER BY timestamp DESC
        LIMIT :limit
    """)
    fun getActivitiesForCharacter(characterId: String, limit: Int = 500): Flow<List<ActivityEntity>>

    /**
     * Get major events only for a character.
     * Used for "While you were away" summary.
     *
     * @param characterId The character's ID
     * @param limit Maximum number of events to return
     * @return Flow of major event activities
     */
    @Query("""
        SELECT * FROM activities
        WHERE characterId = :characterId AND isMajorEvent = 1
        ORDER BY timestamp DESC
        LIMIT :limit
    """)
    fun getMajorEventsForCharacter(characterId: String, limit: Int = 50): Flow<List<ActivityEntity>>

    /**
     * Insert a single activity.
     * Replace on conflict (though ID collision should be rare with auto-increment).
     *
     * @param activity The activity to insert
     * @return Row ID of inserted activity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity): Long

    /**
     * Insert multiple activities in a batch.
     * Used for offline simulation.
     *
     * @param activities List of activities to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)

    /**
     * Get the most recent activity for a character.
     *
     * @param characterId The character's ID
     * @return The latest activity, or null if none exist
     */
    @Query("""
        SELECT * FROM activities
        WHERE characterId = :characterId
        ORDER BY timestamp DESC
        LIMIT 1
    """)
    suspend fun getLatestActivity(characterId: String): ActivityEntity?

    /**
     * Get total count of activities for a character.
     *
     * @param characterId The character's ID
     * @return Total count
     */
    @Query("SELECT COUNT(*) FROM activities WHERE characterId = :characterId")
    suspend fun getActivityCount(characterId: String): Int

    /**
     * Delete old activities to maintain the 500-entry limit.
     * Deletes all but the most recent N activities.
     *
     * @param characterId The character's ID
     * @param keepCount Number of activities to keep
     */
    @Query("""
        DELETE FROM activities
        WHERE characterId = :characterId
        AND id NOT IN (
            SELECT id FROM activities
            WHERE characterId = :characterId
            ORDER BY timestamp DESC
            LIMIT :keepCount
        )
    """)
    suspend fun pruneOldActivities(characterId: String, keepCount: Int = 500)

    /**
     * Delete all activities for a character.
     * Used when a character is deleted.
     *
     * @param characterId The character's ID
     */
    @Query("DELETE FROM activities WHERE characterId = :characterId")
    suspend fun deleteActivitiesForCharacter(characterId: String)

    /**
     * Delete all activities (for testing/debugging).
     */
    @Query("DELETE FROM activities")
    suspend fun deleteAll()
}
