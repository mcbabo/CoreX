package at.mcbabo.corex.data.repositories

import at.mcbabo.corex.data.dao.ExerciseDao
import at.mcbabo.corex.data.models.ExerciseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ExerciseRepository {
    // Basic exercise operations
    fun getAllExercises(): Flow<List<ExerciseModel>>
    suspend fun getExerciseById(id: Long): ExerciseModel?
    suspend fun createExercise(exercise: ExerciseModel): Long
    suspend fun updateExercise(exercise: ExerciseModel)
    suspend fun deleteExercise(exercise: ExerciseModel)

    // Filtered queries
    fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<ExerciseModel>>
    fun getCustomExercises(): Flow<List<ExerciseModel>>
    fun getBuiltInExercises(): Flow<List<ExerciseModel>>
    fun getBodyWeightExercises(): Flow<List<ExerciseModel>>

    // Search and discovery
    suspend fun searchExercises(query: String): List<ExerciseModel>
    suspend fun getAllMuscleGroups(): List<String>

    // Quick actions
    suspend fun createCustomExercise(
        name: String,
        muscleGroup: String,
        description: String? = null
    ): Long

    suspend fun toggleFavorite(exerciseId: Long)
}

class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao
) : ExerciseRepository {

    override fun getAllExercises(): Flow<List<ExerciseModel>> {
        return exerciseDao.getAllExercises()
    }

    override suspend fun getExerciseById(id: Long): ExerciseModel? {
        return exerciseDao.getExerciseById(id)
    }

    override suspend fun createExercise(exercise: ExerciseModel): Long {
        return exerciseDao.insertExercise(exercise)
    }

    override suspend fun updateExercise(exercise: ExerciseModel) {
        exerciseDao.updateExercise(exercise)
    }

    override suspend fun deleteExercise(exercise: ExerciseModel) {
        exerciseDao.deleteExercise(exercise)
    }

    override fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<ExerciseModel>> {
        return exerciseDao.getExercisesByMuscleGroup(muscleGroup)
    }

    override fun getCustomExercises(): Flow<List<ExerciseModel>> {
        return exerciseDao.getExercisesByCustom(true)
    }

    override fun getBuiltInExercises(): Flow<List<ExerciseModel>> {
        return exerciseDao.getExercisesByCustom(false)
    }

    override fun getBodyWeightExercises(): Flow<List<ExerciseModel>> {
        return exerciseDao.getAllExercises().map { exercises ->
            exercises.filter { it.isBodyWeight }
        }
    }

    override suspend fun searchExercises(query: String): List<ExerciseModel> {
        return exerciseDao.getAllExercises().first().filter { exercise ->
            exercise.name.contains(query, ignoreCase = true) ||
                    exercise.muscleGroup.contains(query, ignoreCase = true) ||
                    exercise.description?.contains(query, ignoreCase = true) == true
        }
    }

    override suspend fun getAllMuscleGroups(): List<String> {
        return exerciseDao.getAllMuscleGroups()
    }

    override suspend fun createCustomExercise(
        name: String,
        muscleGroup: String,
        description: String?
    ): Long {
        val exercise = ExerciseModel(
            name = name,
            muscleGroup = muscleGroup,
            description = description,
            isCustom = true
        )
        return exerciseDao.insertExercise(exercise)
    }

    override suspend fun toggleFavorite(exerciseId: Long) {
        val exercise = exerciseDao.getExerciseById(exerciseId)
        exercise?.let {
            // You'd need to add a favorite field to ExerciseModel
            // exerciseDao.updateExercise(it.copy(isFavorite = isFavorite))
        }
    }
}