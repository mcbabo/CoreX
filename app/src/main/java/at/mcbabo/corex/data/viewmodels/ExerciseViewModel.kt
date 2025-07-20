package at.mcbabo.corex.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.mcbabo.corex.data.models.ExerciseModel
import at.mcbabo.corex.data.repositories.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {
    // Cache the muscle groups immediately
    private val _muscleGroups = MutableStateFlow<List<String>>(emptyList())
    val muscleGroups = _muscleGroups.asStateFlow()

    private val _selectedMuscleGroup = MutableStateFlow<String?>(null)
    val selectedMuscleGroup = _selectedMuscleGroup.asStateFlow()

    // Get all exercises WITHOUT filtering - let the screen handle filtering
    val allExercises: Flow<List<ExerciseModel>> = exerciseRepository.getAllExercises()

    // Keep the filtered version if you need it elsewhere
    val exercises: Flow<List<ExerciseModel>> = combine(
        allExercises,
        selectedMuscleGroup
    ) { exercises, muscleGroup ->
        exercises.filter { exercise ->
            muscleGroup?.let { exercise.muscleGroup == it } != false
        }
    }

    val customExercises: Flow<List<ExerciseModel>> = exerciseRepository.getCustomExercises()

    init {
        // Load muscle groups immediately when ViewModel is created
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

    fun clearFilters() {
        _selectedMuscleGroup.value = null
    }
    
    fun createCustomExercise(name: String, muscleGroup: String, description: String? = null) {
        viewModelScope.launch {
            exerciseRepository.createCustomExercise(name, muscleGroup, description)
        }
    }

    fun updateExercise(exercise: ExerciseModel) {
        viewModelScope.launch {
            exerciseRepository.updateExercise(exercise)
        }
    }

    fun deleteExercise(exercise: ExerciseModel) {
        viewModelScope.launch {
            exerciseRepository.deleteExercise(exercise)
        }
    }

    fun getExerciseById(id: Long): Flow<ExerciseModel?> = flow {
        emit(exerciseRepository.getExerciseById(id))
    }.flowOn(Dispatchers.IO)
}