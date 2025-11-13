package com.watxaut.rolenonplayinggame.data.repository

import com.watxaut.rolenonplayinggame.data.local.dao.ActivityDao
import com.watxaut.rolenonplayinggame.data.local.entity.ActivityEntity
import com.watxaut.rolenonplayinggame.domain.model.Activity
import com.watxaut.rolenonplayinggame.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of ActivityRepository using Room database.
 *
 * Per Clean Architecture:
 * - Domain layer defines interface
 * - Data layer implements it
 * - Uses ActivityDao for persistence
 */
class ActivityRepositoryImpl @Inject constructor(
    private val activityDao: ActivityDao
) : ActivityRepository {

    override fun getActivitiesForCharacter(characterId: String, limit: Int): Flow<List<Activity>> {
        return activityDao.getActivitiesForCharacter(characterId, limit)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getMajorEventsForCharacter(characterId: String, limit: Int): Flow<List<Activity>> {
        return activityDao.getMajorEventsForCharacter(characterId, limit)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun logActivity(activity: Activity): Result<Unit> {
        return try {
            val entity = ActivityEntity.fromDomain(activity)
            activityDao.insertActivity(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logActivities(activities: List<Activity>): Result<Unit> {
        return try {
            val entities = activities.map { ActivityEntity.fromDomain(it) }
            activityDao.insertActivities(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pruneOldActivities(characterId: String, keepCount: Int): Result<Unit> {
        return try {
            activityDao.pruneOldActivities(characterId, keepCount)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLatestActivity(characterId: String): Activity? {
        return try {
            activityDao.getLatestActivity(characterId)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deleteActivitiesForCharacter(characterId: String): Result<Unit> {
        return try {
            activityDao.deleteActivitiesForCharacter(characterId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
