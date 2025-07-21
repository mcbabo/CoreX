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
import java.util.Date
import javax.inject.Inject

// WorkoutRepository - Everything about managing workouts and training sessions
interface WorkoutRepository {
    // Basic workout operations
    fun getAllWorkouts(): Flow<List<WorkoutModel>>
    fun getActiveWorkouts(): Flow<List<WorkoutModel>>
    suspend fun getWorkoutById(id: Long): WorkoutModel?
    suspend fun createWorkout(workout: WorkoutModel): Long
    suspend fun updateWorkout(workout: WorkoutModel)
    suspend fun deleteWorkout(workout: WorkoutModel)

    // Full workout with exercises and progressions
    suspend fun getFullWorkout(id: Long): Workout?
    fun observeFullWorkout(id: Long): Flow<Workout?>

    // Workout session management
    suspend fun startWorkout(workoutId: Long)
    suspend fun completeWorkout(workoutId: Long)
    suspend fun resetWorkoutProgress(workoutId: Long)

    // Exercise management within workouts
    suspend fun addExerciseToWorkout(workoutId: Long, exerciseId: Long, orderIndex: Int): Long
    suspend fun removeExerciseFromWorkout(workoutExerciseId: Long)
    suspend fun updateExerciseOrder(exerciseOrders: List<Pair<Long, Int>>)
    suspend fun updateExerciseTargets(
        workoutExerciseId: Long,
        targetWeight: Float?,
        targetReps: Int?,
        targetSets: Int?
    )

    // Exercise completion tracking
    suspend fun markExerciseCompleted(workoutExerciseId: Long, isCompleted: Boolean)
    suspend fun recordWeight(workoutExerciseId: Long, weight: Float, notes: String? = null): Long

    // Analytics
    suspend fun getWorkoutStats(): WorkoutStats
    suspend fun getRecentWorkoutHistory(): List<WorkoutSession>

    fun getWorkoutSummaries(): Flow<List<WorkoutSummary>>
}

data class WorkoutStats(
    val totalCompletions: Int,
    val averageCompletion: Float, // percentage
    val lastCompleted: Date?,
    val totalExercises: Int,
    val averageWeight: Float?
)

data class WorkoutSession(
    val workoutId: Long,
    val workoutName: String,
    val completedDate: Date,
    val completionPercentage: Float,
    val totalExercises: Int,
    val completedExercises: Int
)

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val weightProgressionDao: WeightProgressionDao,
    private val settingsRepository: SettingsRepository // Inject settings repository
) : WorkoutRepository {

    override fun getAllWorkouts(): Flow<List<WorkoutModel>> {
        return workoutDao.getAllFullWorkouts().map { workouts ->
            workouts.map { it.workout }
        }
    }

    override fun getActiveWorkouts(): Flow<List<WorkoutModel>> {
        return workoutDao.getActiveWorkouts()
    }

    override suspend fun getWorkoutById(id: Long): WorkoutModel? {
        return workoutDao.getWorkoutById(id)
    }

    override suspend fun createWorkout(workout: WorkoutModel): Long {
        return workoutDao.insertWorkout(workout)
    }

    override suspend fun updateWorkout(workout: WorkoutModel) {
        workoutDao.updateWorkout(workout)
    }

    override suspend fun deleteWorkout(workout: WorkoutModel) {
        workoutDao.deleteWorkout(workout)
    }

    override suspend fun getFullWorkout(id: Long): Workout? {
        return workoutDao.getFullWorkout(id)
    }

    override fun observeFullWorkout(id: Long): Flow<Workout?> = workoutDao.observeFullWorkout(id)

    override suspend fun startWorkout(workoutId: Long) {
        // Reset all exercise completion status
        workoutExerciseDao.resetWorkoutCompletion(workoutId)
    }

    override suspend fun completeWorkout(workoutId: Long) {
        // Mark all exercises as completed
        val exercises = workoutExerciseDao.getWorkoutExercisesByWorkoutId(workoutId)
        exercises.forEach { exercise ->
            workoutExerciseDao.updateCompletionStatus(exercise.id, true)
        }
    }

    override suspend fun resetWorkoutProgress(workoutId: Long) {
        workoutExerciseDao.resetWorkoutCompletion(workoutId)
    }

    override suspend fun addExerciseToWorkout(
        workoutId: Long,
        exerciseId: Long,
        orderIndex: Int
    ): Long {
        val settings = settingsRepository.getCurrentSettings()
        val workoutExercise = WorkoutExerciseModel(
            workoutId = workoutId,
            exerciseId = exerciseId,
            orderIndex = orderIndex,
            targetReps = settings.defaultReps,
            targetSets = settings.defaultSets
        )
        return workoutExerciseDao.insertWorkoutExercise(workoutExercise)
    }

    override suspend fun removeExerciseFromWorkout(workoutExerciseId: Long) {
        val workoutExercise = workoutExerciseDao.getWorkoutExerciseById(workoutExerciseId)
        workoutExercise?.let {
            workoutExerciseDao.deleteWorkoutExercise(it)
        }
    }

    override suspend fun updateExerciseOrder(
        exerciseOrders: List<Pair<Long, Int>>
    ) {
        exerciseOrders.forEach { (exerciseId, newOrder) ->
            val workoutExercise = workoutExerciseDao.getWorkoutExerciseById(exerciseId)
            workoutExercise?.let {
                workoutExerciseDao.updateWorkoutExercise(it.copy(orderIndex = newOrder))
            }
        }
    }

    override suspend fun updateExerciseTargets(
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

    override suspend fun markExerciseCompleted(workoutExerciseId: Long, isCompleted: Boolean) {
        workoutExerciseDao.updateCompletionStatus(workoutExerciseId, isCompleted)
    }

    override suspend fun recordWeight(
        workoutExerciseId: Long,
        weight: Float,
        notes: String?
    ): Long {
        val progression = WeightProgressionModel(
            workoutExerciseId = workoutExerciseId,
            weight = weight,
            notes = notes
        )
        weightProgressionDao.updateTargetWeight(
            workoutExerciseId,
            weight
        )
        return weightProgressionDao.insertWeightProgression(progression)
    }

    override suspend fun getWorkoutStats(): WorkoutStats {
        // Implementation would calculate stats from database
        return WorkoutStats(
            totalCompletions = 0,
            averageCompletion = 0f,
            lastCompleted = null,
            totalExercises = 0,
            averageWeight = null
        )
    }

    override suspend fun getRecentWorkoutHistory(): List<WorkoutSession> {
        // Implementation would fetch recent workout sessions
        return emptyList()
    }

    override fun getWorkoutSummaries(): Flow<List<WorkoutSummary>> {
        return workoutDao.getWorkoutSummaries()
    }
}
