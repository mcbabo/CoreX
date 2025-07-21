package at.mcbabo.corex.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import at.mcbabo.corex.data.models.WeightProgressionModel
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface WeightProgressionDao {
    // Basic CRUD
    @Upsert
    suspend fun insertWeightProgression(progression: WeightProgressionModel): Long

    @Update
    suspend fun updateWeightProgression(progression: WeightProgressionModel)

    @Delete
    suspend fun deleteWeightProgression(progression: WeightProgressionModel)

    // Simple queries
    @Query("SELECT * FROM weight_progressions WHERE id = :id")
    suspend fun getWeightProgressionById(id: Long): WeightProgressionModel?

    @Query("SELECT * FROM weight_progressions WHERE workoutExerciseId = :workoutExerciseId ORDER BY date DESC")
    fun getProgressionsByWorkoutExercise(workoutExerciseId: Long): Flow<List<WeightProgressionModel>>

    @Query("SELECT * FROM weight_progressions WHERE workoutExerciseId = :workoutExerciseId ORDER BY date DESC LIMIT 1")
    suspend fun getLatestProgression(workoutExerciseId: Long): WeightProgressionModel?

    @Query("SELECT * FROM weight_progressions WHERE workoutExerciseId = :workoutExerciseId ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentProgressions(
        workoutExerciseId: Long,
        limit: Int
    ): List<WeightProgressionModel>

    // Date range queries
    @Query("SELECT * FROM weight_progressions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getProgressionsByDateRange(
        startDate: Date,
        endDate: Date
    ): List<WeightProgressionModel>

    // Analytics
    @Query("SELECT AVG(weight) FROM weight_progressions WHERE workoutExerciseId = :workoutExerciseId")
    suspend fun getAverageWeight(workoutExerciseId: Long): Float?

    @Query("SELECT MAX(weight) FROM weight_progressions WHERE workoutExerciseId = :workoutExerciseId")
    suspend fun getMaxWeight(workoutExerciseId: Long): Float?

    @Query("UPDATE workout_exercises SET targetWeight = :targetWeight WHERE id = :workoutExerciseId")
    suspend fun updateTargetWeight(workoutExerciseId: Long, targetWeight: Float)
}