package at.mcbabo.corex.data.repositories

import at.mcbabo.corex.data.dao.WeightProgressionDao
import at.mcbabo.corex.data.models.WeightProgressionModel
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

interface ProgressRepository {
    // Basic progression operations
    fun getProgressionsForExercise(workoutExerciseId: Long): Flow<List<WeightProgressionModel>>
    suspend fun getLatestProgression(workoutExerciseId: Long): WeightProgressionModel?
    suspend fun addProgression(progression: WeightProgressionModel): Long
    suspend fun updateProgression(progression: WeightProgressionModel)
    suspend fun deleteProgression(progression: WeightProgressionModel)

    // Analytics and insights
    suspend fun getProgressStats(workoutExerciseId: Long): ProgressStats
    suspend fun getProgressTrend(workoutExerciseId: Long, days: Int = 30): ProgressTrend
    suspend fun getPersonalRecords(exerciseId: Long): List<PersonalRecord>

    // Date-based queries
    suspend fun getProgressionsByDateRange(
        startDate: Date,
        endDate: Date
    ): List<WeightProgressionModel>

    suspend fun getWeeklyProgress(workoutExerciseId: Long): List<WeeklyProgressSummary>
}

data class ProgressStats(
    val maxWeight: Float?,
    val averageWeight: Float?,
    val totalSessions: Int,
    val improvementRate: Float, // weight increase per session
    val lastRecorded: Date?
)

data class ProgressTrend(
    val direction: TrendDirection,
    val changePercentage: Float,
    val isConsistent: Boolean
)

enum class TrendDirection { IMPROVING, DECLINING, STABLE }

data class PersonalRecord(
    val weight: Float,
    val date: Date,
    val notes: String?
)

data class WeeklyProgressSummary(
    val weekStart: Date,
    val sessionsCount: Int,
    val averageWeight: Float,
    val maxWeight: Float
)

class ProgressRepositoryImpl @Inject constructor(
    private val progressionDao: WeightProgressionDao
) : ProgressRepository {

    override fun getProgressionsForExercise(workoutExerciseId: Long): Flow<List<WeightProgressionModel>> {
        return progressionDao.getProgressionsByWorkoutExercise(workoutExerciseId)
    }

    override suspend fun getLatestProgression(workoutExerciseId: Long): WeightProgressionModel? {
        return progressionDao.getLatestProgression(workoutExerciseId)
    }

    override suspend fun addProgression(progression: WeightProgressionModel): Long {
        return progressionDao.insertWeightProgression(progression)
    }

    override suspend fun updateProgression(progression: WeightProgressionModel) {
        progressionDao.updateWeightProgression(progression)
    }

    override suspend fun deleteProgression(progression: WeightProgressionModel) {
        progressionDao.deleteWeightProgression(progression)
    }

    override suspend fun getProgressStats(workoutExerciseId: Long): ProgressStats {
        val maxWeight = progressionDao.getMaxWeight(workoutExerciseId)
        val avgWeight = progressionDao.getAverageWeight(workoutExerciseId)
        val recentProgressions = progressionDao.getRecentProgressions(workoutExerciseId, 10)

        val improvementRate = if (recentProgressions.size >= 2) {
            val oldestWeight = recentProgressions.last().weight
            val newestWeight = recentProgressions.first().weight
            (newestWeight - oldestWeight) / recentProgressions.size
        } else 0f

        return ProgressStats(
            maxWeight = maxWeight,
            averageWeight = avgWeight,
            totalSessions = recentProgressions.size,
            improvementRate = improvementRate,
            lastRecorded = recentProgressions.firstOrNull()?.date
        )
    }

    override suspend fun getProgressTrend(workoutExerciseId: Long, days: Int): ProgressTrend {
        val progressions = progressionDao.getRecentProgressions(workoutExerciseId, days)

        if (progressions.size < 3) {
            return ProgressTrend(TrendDirection.STABLE, 0f, false)
        }

        val recent = progressions.take(progressions.size / 2)
        val older = progressions.drop(progressions.size / 2)

        val recentAvg = recent.map { it.weight }.average()
        val olderAvg = older.map { it.weight }.average()

        val changePercentage = ((recentAvg - olderAvg) / olderAvg * 100).toFloat()

        val direction = when {
            changePercentage > 5 -> TrendDirection.IMPROVING
            changePercentage < -5 -> TrendDirection.DECLINING
            else -> TrendDirection.STABLE
        }

        return ProgressTrend(direction, changePercentage, true)
    }

    override suspend fun getPersonalRecords(exerciseId: Long): List<PersonalRecord> {
        // Implementation would find PRs for this exercise across all workouts
        return emptyList()
    }

    override suspend fun getProgressionsByDateRange(
        startDate: Date,
        endDate: Date
    ): List<WeightProgressionModel> {
        return progressionDao.getProgressionsByDateRange(startDate, endDate)
    }

    override suspend fun getWeeklyProgress(workoutExerciseId: Long): List<WeeklyProgressSummary> {
        // Implementation would group progressions by week
        return emptyList()
    }
}