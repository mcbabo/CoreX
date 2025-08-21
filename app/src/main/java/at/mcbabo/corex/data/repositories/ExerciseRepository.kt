package at.mcbabo.corex.data.repositories

import at.mcbabo.corex.data.dao.ExerciseDao
import at.mcbabo.corex.data.dao.WeightProgressionDao
import at.mcbabo.corex.data.models.ExerciseModel
import at.mcbabo.corex.data.models.WeightProgressionModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val weightProgressionDao: WeightProgressionDao,
) {
    fun getAllExercises(): Flow<List<ExerciseModel>> = exerciseDao.getAllExercises()

    suspend fun getExerciseById(id: Long): ExerciseModel? = exerciseDao.getExerciseById(id)

    suspend fun createExercise(exercise: ExerciseModel): Long = exerciseDao.insertExercise(exercise)

    suspend fun updateExercise(exercise: ExerciseModel) {
        exerciseDao.updateExercise(exercise)
    }

    suspend fun deleteExercise(exercise: ExerciseModel) {
        exerciseDao.deleteExercise(exercise)
    }

    suspend fun getAllMuscleGroups(): List<String> = exerciseDao.getAllMuscleGroups()

    fun getWeightProgressionByExercise(exerciseId: Long): Flow<List<WeightProgressionModel>> {
        return weightProgressionDao.getProgressionsByExercise(exerciseId)
    }
}
