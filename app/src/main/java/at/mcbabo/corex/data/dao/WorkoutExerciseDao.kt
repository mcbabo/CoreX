package at.mcbabo.corex.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import at.mcbabo.corex.data.models.WorkoutExerciseModel

@Dao
interface WorkoutExerciseDao {
    // Basic CRUD
    @Upsert
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExerciseModel): Long

    @Update
    suspend fun updateWorkoutExercise(workoutExercise: WorkoutExerciseModel)

    @Delete
    suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExerciseModel)

    // Simple queries
    @Query("SELECT * FROM workout_exercises WHERE id = :id")
    suspend fun getWorkoutExerciseById(id: Long): WorkoutExerciseModel?

    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY orderIndex")
    suspend fun getWorkoutExercisesByWorkoutId(workoutId: Long): List<WorkoutExerciseModel>

    // Mark as completed
    @Query("UPDATE workout_exercises SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletionStatus(id: Long, isCompleted: Boolean)

    // Bulk operations
    @Query("UPDATE workout_exercises SET isCompleted = 0 WHERE workoutId = :workoutId")
    suspend fun resetWorkoutCompletion(workoutId: Long)
}