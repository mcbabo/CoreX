package at.mcbabo.corex.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.mcbabo.corex.data.entities.Workout
import at.mcbabo.corex.data.entities.WorkoutSummary
import at.mcbabo.corex.data.models.ExerciseModel
import at.mcbabo.corex.data.models.WeightProgressionModel
import at.mcbabo.corex.data.models.WorkoutModel
import at.mcbabo.corex.data.repositories.ProgressRepository
import at.mcbabo.corex.data.repositories.ProgressStats
import at.mcbabo.corex.data.repositories.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    // Workout List functionality
    fun getWorkouts(): Flow<List<WorkoutModel>> = workoutRepository.getActiveWorkouts()

    suspend fun createWorkoutWithExercises(
        name: String,
        weekday: String,
        exercises: List<ExerciseModel>
    ): Result<Long> {
        return try {
            val workoutId = createWorkoutAndGetId(name, weekday)
            exercises.forEachIndexed { index, exercise ->
                addExerciseToWorkout(workoutId, exercise.id)
            }
            Result.success(workoutId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun createWorkout(name: String, weekday: String) {
        viewModelScope.launch {
            val workout = WorkoutModel(
                name = name,
                weekday = weekday,
                isActive = true
            )
            workoutRepository.createWorkout(workout)
        }
    }

    fun deleteWorkout(workout: WorkoutModel) {
        viewModelScope.launch {
            workoutRepository.deleteWorkout(workout)
        }
    }

    // Workout Detail functionality
    fun getWorkoutDetails(id: Long): Flow<Workout?> = workoutRepository.observeFullWorkout(id)

    fun addExerciseToWorkout(workoutId: Long, exerciseId: Long) {
        viewModelScope.launch {
            // Get current exercise count for ordering
            val workout = workoutRepository.getFullWorkout(workoutId)
            val orderIndex = workout?.exercises?.size ?: 0
            workoutRepository.addExerciseToWorkout(workoutId, exerciseId, orderIndex)
        }
    }

    fun removeExerciseFromWorkout(workoutExerciseId: Long) {
        viewModelScope.launch {
            workoutRepository.removeExerciseFromWorkout(workoutExerciseId)
        }
    }

    fun updateExerciseTargets(
        workoutExerciseId: Long,
        targetWeight: Float?,
        targetReps: Int?,
        targetSets: Int?
    ) {
        viewModelScope.launch {
            workoutRepository.updateExerciseTargets(
                workoutExerciseId,
                targetWeight,
                targetReps,
                targetSets
            )
        }
    }

    // Workout Session functionality
    fun startWorkout(workoutId: Long) {
        viewModelScope.launch {
            workoutRepository.startWorkout(workoutId)
        }
    }

    fun completeWorkout(workoutId: Long) {
        viewModelScope.launch {
            workoutRepository.completeWorkout(workoutId)
        }
    }

    fun markExerciseCompleted(workoutExerciseId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            workoutRepository.markExerciseCompleted(workoutExerciseId, isCompleted)
        }
    }

    fun recordWeight(workoutExerciseId: Long, weight: Float, notes: String? = null) {
        viewModelScope.launch {
            workoutRepository.recordWeight(workoutExerciseId, weight, notes)
        }
    }

    fun resetWorkoutProgress(workoutId: Long) {
        viewModelScope.launch {
            workoutRepository.resetWorkoutProgress(workoutId)
        }
    }

    // Progress tracking
    fun getProgressForExercise(workoutExerciseId: Long): Flow<List<WeightProgressionModel>> {
        return progressRepository.getProgressionsForExercise(workoutExerciseId)
    }

    fun getProgressStats(workoutExerciseId: Long): Flow<ProgressStats?> = flow {
        emit(progressRepository.getProgressStats(workoutExerciseId))
    }.flowOn(Dispatchers.IO)

    fun getWorkoutSummaries(): Flow<List<WorkoutSummary>> = workoutRepository.getWorkoutSummaries()

    suspend fun createWorkoutAndGetId(name: String, weekday: String): Long {
        val workout = WorkoutModel(
            name = name,
            weekday = weekday,
            isActive = true
        )
        return workoutRepository.createWorkout(workout)
    }
}