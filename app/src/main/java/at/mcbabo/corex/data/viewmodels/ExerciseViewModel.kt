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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedMuscleGroup = MutableStateFlow<String?>(null)
    val selectedMuscleGroup = _selectedMuscleGroup.asStateFlow()

    // Get all exercises with real-time filtering
    val exercises: Flow<List<ExerciseModel>> = combine(
        exerciseRepository.getAllExercises(),
        searchQuery,
        selectedMuscleGroup
    ) { exercises, query, muscleGroup ->
        exercises.filter { exercise ->
            val matchesSearch = if (query.isBlank()) {
                true
            } else {
                exercise.name.contains(query, ignoreCase = true) ||
                        exercise.muscleGroup.contains(query, ignoreCase = true)
            }

            val matchesMuscleGroup = muscleGroup?.let {
                exercise.muscleGroup == it
            } != false

            matchesSearch && matchesMuscleGroup
        }
    }

    // Get muscle groups for filter dropdown
    val muscleGroups: Flow<List<String>> = flow {
        emit(exerciseRepository.getAllMuscleGroups())
    }.flowOn(Dispatchers.IO)

    // Get custom exercises only
    val customExercises: Flow<List<ExerciseModel>> = exerciseRepository.getCustomExercises()

    fun searchExercises(query: String) {
        _searchQuery.value = query
    }

    fun filterByMuscleGroup(muscleGroup: String?) {
        _selectedMuscleGroup.value = muscleGroup
    }

    fun clearFilters() {
        _searchQuery.value = ""
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