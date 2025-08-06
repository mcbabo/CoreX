package at.mcbabo.corex.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import at.mcbabo.corex.data.models.WorkoutExerciseModel
import at.mcbabo.corex.data.models.WorkoutModel
import at.mcbabo.corex.data.models.WorkoutWeekdayModel

data class Workout(
    @Embedded val workout: WorkoutModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId",
        entity = WorkoutExerciseModel::class
    )
    val exercises: List<WorkoutExercise> = emptyList(),
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    val weekdays: List<WorkoutWeekdayModel> = emptyList()
)

// Helper data class for raw queries (without weekdays populated)
data class WorkoutSummaryRaw(
    val id: Long,
    val name: String,
    val isActive: Boolean,
    val exerciseCount: Int
)

data class WorkoutSummary(
    val id: Long,
    val name: String,
    val isActive: Boolean,
    val exerciseCount: Int,
    val weekdays: List<Int> = emptyList() // List of weekday integers (0=Monday, 1=Tuesday, etc.)
)
