package at.mcbabo.corex.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import at.mcbabo.corex.data.models.WorkoutExerciseModel
import at.mcbabo.corex.data.models.WorkoutModel

data class Workout(
    @Embedded val workout: WorkoutModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId",
        entity = WorkoutExerciseModel::class
    )
    val exercises: List<WorkoutExercise> = emptyList()
)

data class WorkoutSummary(
    val id: Long,
    val name: String,
    val weekday: Int,
    val isActive: Boolean,
    val exerciseCount: Int
)
