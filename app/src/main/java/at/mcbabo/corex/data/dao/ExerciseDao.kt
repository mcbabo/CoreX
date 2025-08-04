package at.mcbabo.corex.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import at.mcbabo.corex.data.models.ExerciseModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    // Basic CRUD
    @Upsert
    suspend fun insertExercise(exercise: ExerciseModel): Long

    @Update
    suspend fun updateExercise(exercise: ExerciseModel)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseModel)

    // Simple queries
    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: Long): ExerciseModel?

    @Query("SELECT * FROM exercises ORDER BY name")
    fun getAllExercises(): Flow<List<ExerciseModel>>

    @Query("SELECT * FROM exercises WHERE muscleGroup = :muscleGroup ORDER BY name")
    fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<ExerciseModel>>

    @Query("SELECT * FROM exercises WHERE isCustom = :isCustom ORDER BY name")
    fun getExercisesByCustom(isCustom: Boolean): Flow<List<ExerciseModel>>

    @Query("SELECT DISTINCT muscleGroup FROM exercises ORDER BY muscleGroup")
    suspend fun getAllMuscleGroups(): List<String>
}
