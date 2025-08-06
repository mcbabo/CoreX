package at.mcbabo.corex.data.repositories

import at.mcbabo.corex.data.dao.WeightProgressionDao
import at.mcbabo.corex.data.dao.WorkoutDao
import at.mcbabo.corex.data.dao.WorkoutExerciseDao
import at.mcbabo.corex.data.entities.Workout
import at.mcbabo.corex.data.entities.WorkoutSummary
import at.mcbabo.corex.data.models.WeightProgressionModel
import at.mcbabo.corex.data.models.WorkoutExerciseModel
import at.mcbabo.corex.data.models.WorkoutModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val weightProgressionDao: WeightProgressionDao,
    private val settingsRepository: SettingsRepository
) {
    fun getAllWorkouts(): Flow<List<WorkoutModel>> = workoutDao.getAllFullWorkouts().map { workouts ->
        workouts.map { it.workout }
    }

    fun getActiveWorkouts(): Flow<List<WorkoutModel>> = workoutDao.getActiveWorkouts()

    suspend fun getWorkoutsByWeekday(weekday: Int): List<WorkoutModel> =
        workoutDao.getWorkoutsByWeekday(weekday)

    suspend fun getWorkoutById(id: Long): WorkoutModel? = workoutDao.getWorkoutById(id)

    suspend fun createWorkout(workout: WorkoutModel): Long = workoutDao.insertWorkout(workout)

    suspend fun updateWorkout(workout: WorkoutModel) {
        workoutDao.updateWorkout(workout)
    }

    suspend fun deleteWorkout(workout: WorkoutModel) {
        workoutDao.deleteWorkout(workout)
    }

    suspend fun getFullWorkout(id: Long): Workout? = workoutDao.getFullWorkout(id)

    fun observeFullWorkout(id: Long): Flow<Workout?> = workoutDao.observeFullWorkout(id)

    suspend fun completeWorkout(workoutId: Long) {
        val exercises = workoutExerciseDao.getWorkoutExercisesByWorkoutId(workoutId)
        exercises.forEach { exercise ->
            workoutExerciseDao.updateCompletionStatus(exercise.id, true)
        }
    }

    suspend fun resetWorkoutProgress(workoutId: Long) {
        workoutExerciseDao.resetWorkoutCompletion(workoutId)
    }

    suspend fun addExerciseToWorkout(workoutId: Long, exerciseId: Long, orderIndex: Int): Long {
        val settings = settingsRepository.getCurrentSettings()
        val workoutExercise =
            WorkoutExerciseModel(
                workoutId = workoutId,
                exerciseId = exerciseId,
                orderIndex = orderIndex,
                targetReps = settings.defaultReps,
                targetSets = settings.defaultSets
            )
        return workoutExerciseDao.insertWorkoutExercise(workoutExercise)
    }

    suspend fun removeExerciseFromWorkout(workoutExerciseId: Long) {
        val workoutExercise = workoutExerciseDao.getWorkoutExerciseById(workoutExerciseId)
        workoutExercise?.let {
            workoutExerciseDao.deleteWorkoutExercise(it)
        }
    }

    suspend fun updateExerciseOrder(exerciseOrders: List<Pair<Long, Int>>) {
        exerciseOrders.forEach { (exerciseId, newOrder) ->
            val workoutExercise = workoutExerciseDao.getWorkoutExerciseById(exerciseId)
            workoutExercise?.let {
                workoutExerciseDao.updateWorkoutExercise(it.copy(orderIndex = newOrder))
            }
        }
    }

    suspend fun updateExerciseTargets(
        workoutExerciseId: Long,
        targetWeight: Float?,
        targetReps: Int?,
        targetSets: Int?
    ) {
        val workoutExercise = workoutExerciseDao.getWorkoutExerciseById(workoutExerciseId)
        workoutExercise?.let {
            workoutExerciseDao.updateWorkoutExercise(
                it.copy(
                    targetWeight = targetWeight,
                    targetReps = targetReps,
                    targetSets = targetSets
                )
            )
        }
    }

    suspend fun markExerciseCompleted(workoutExerciseId: Long, isCompleted: Boolean) {
        workoutExerciseDao.updateCompletionStatus(workoutExerciseId, isCompleted)
    }

    suspend fun recordWeight(workoutExerciseId: Long, exerciseId: Long, weight: Float, notes: String?): Long {
        val progression =
            WeightProgressionModel(
                exerciseId = exerciseId,
                weight = weight,
                notes = notes
            )
        weightProgressionDao.updateTargetWeight(
            workoutExerciseId,
            weight
        )
        return weightProgressionDao.insertWeightProgression(progression)
    }

    fun getWorkoutSummaries(): Flow<List<WorkoutSummary>> = workoutDao.getWorkoutSummaries()

    suspend fun insertWorkoutWithWeekdays(workout: WorkoutModel, weekdays: List<Int>): Long {
        return workoutDao.insertWorkoutWithWeekdays(workout, weekdays)
    }

    suspend fun updateWorkoutWeekdays(workoutId: Long, weekdays: List<Int>) {
        workoutDao.updateWorkoutWeekdays(workoutId, weekdays)
    }
}
