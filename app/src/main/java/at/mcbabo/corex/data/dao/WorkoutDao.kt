package at.mcbabo.corex.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import at.mcbabo.corex.data.entities.Workout
import at.mcbabo.corex.data.entities.WorkoutSummary
import at.mcbabo.corex.data.models.WorkoutModel
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    // Basic CRUD
    @Upsert
    suspend fun insertWorkout(workout: WorkoutModel): Long

    @Update
    suspend fun updateWorkout(workout: WorkoutModel)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutModel)

    // Simple queries
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): WorkoutModel?

    @Query("SELECT * FROM workouts WHERE isActive = 1 ORDER BY weekday")
    fun getActiveWorkouts(): Flow<List<WorkoutModel>>

    @Query("SELECT * FROM workouts WHERE weekday = :weekday AND isActive = 1")
    suspend fun getWorkoutsByWeekday(weekday: String): List<WorkoutModel>

    // With relations
    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getFullWorkout(id: Long): Workout?

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    fun observeFullWorkout(id: Long): Flow<Workout?>

    @Transaction
    @Query("SELECT * FROM workouts WHERE isActive = 1 ORDER BY weekday")
    fun getAllFullWorkouts(): Flow<List<Workout>>

    @Query(
        """
            SELECT 
                w.id,
                w.name,
                w.weekday,
                w.isActive,
                COUNT(we.id) as exerciseCount
            FROM workouts w
            LEFT JOIN workout_exercises we ON w.id = we.workoutId
            WHERE w.isActive = 1
            GROUP BY w.id, w.name, w.weekday, w.isActive
            ORDER BY w.weekday
        """
    )
    fun getWorkoutSummaries(): Flow<List<WorkoutSummary>>
}
