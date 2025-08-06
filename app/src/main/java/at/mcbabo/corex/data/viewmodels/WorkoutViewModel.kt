package at.mcbabo.corex.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.mcbabo.corex.data.entities.Workout
import at.mcbabo.corex.data.entities.WorkoutSummary
import at.mcbabo.corex.data.models.ExerciseModel
import at.mcbabo.corex.data.models.WorkoutModel
import at.mcbabo.corex.data.repositories.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(private val workoutRepository: WorkoutRepository) : ViewModel() {
    suspend fun createWorkoutWithExercises(
        name: String,
        weekdays: List<Int>,
        exercises: List<ExerciseModel>
    ): Result<Long> =
        try {
            val workoutId = workoutRepository.insertWorkoutWithWeekdays(
                WorkoutModel(
                    name = name,
                    isActive = true
                ),
                weekdays = weekdays
            )
            exercises.forEachIndexed { index, exercise ->
                addExerciseToWorkout(workoutId, exercise.id)
            }
            Result.success(workoutId)
        } catch (e: Exception) {
            Result.failure(e)
        }

    fun deleteWorkout(workout: WorkoutModel) {
        viewModelScope.launch {
            workoutRepository.deleteWorkout(workout)
        }
    }

    fun updateWorkout(workout: WorkoutModel, weekdays: List<Int>) {
        viewModelScope.launch {
            workoutRepository.updateWorkout(workout)
            workoutRepository.updateWorkoutWeekdays(workout.id, weekdays)
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

    fun markExerciseCompleted(workoutExerciseId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            workoutRepository.markExerciseCompleted(workoutExerciseId, isCompleted)
        }
    }

    fun recordWeight(workoutExerciseId: Long, exerciseId: Long, weight: Float, notes: String? = null) {
        viewModelScope.launch {
            workoutRepository.recordWeight(workoutExerciseId, exerciseId, weight, notes)
        }
    }

    fun resetWorkoutProgress(workoutId: Long) {
        viewModelScope.launch {
            workoutRepository.resetWorkoutProgress(workoutId)
        }
    }

    fun getWorkoutSummaries(): Flow<List<WorkoutSummary>> = workoutRepository.getWorkoutSummaries()
}
