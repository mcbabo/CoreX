package at.mcbabo.corex.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.mcbabo.corex.data.models.ExerciseModel
import at.mcbabo.corex.data.repositories.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(private val exerciseRepository: ExerciseRepository) : ViewModel() {
    // Cache the muscle groups immediately
    private val _muscleGroups = MutableStateFlow<List<String>>(emptyList())
    val muscleGroups = _muscleGroups.asStateFlow()

    private val _selectedMuscleGroup = MutableStateFlow<String?>(null)
    val selectedMuscleGroup = _selectedMuscleGroup.asStateFlow()

    val allExercises: Flow<List<ExerciseModel>> = exerciseRepository.getAllExercises()

    val exercises: Flow<List<ExerciseModel>> =
        combine(
            allExercises,
            selectedMuscleGroup
        ) { exercises, muscleGroup ->
            exercises.filter { exercise ->
                muscleGroup?.let { exercise.muscleGroup == it } != false
            }
        }

    init {
        loadMuscleGroups()
    }

    private fun loadMuscleGroups() {
        viewModelScope.launch {
            _muscleGroups.value = exerciseRepository.getAllMuscleGroups()
        }
    }

    fun filterByMuscleGroup(muscleGroup: String?) {
        _selectedMuscleGroup.value = muscleGroup
    }

    fun deleteExercise(exercise: ExerciseModel) {
        viewModelScope.launch {
            exerciseRepository.deleteExercise(exercise)
        }
    }

    fun createExercise(exercise: ExerciseModel) {
        viewModelScope.launch {
            exerciseRepository.createExercise(exercise)
        }
    }
}
