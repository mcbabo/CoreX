package at.mcbabo.corex.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import at.mcbabo.corex.data.models.ExerciseModel
import at.mcbabo.corex.data.models.WeightProgressionModel
import at.mcbabo.corex.data.models.WorkoutExerciseModel

data class WorkoutExercise(
    @Embedded val workoutExercise: WorkoutExerciseModel,

    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "id"
    )
    val exercise: ExerciseModel,

    @Relation(
        parentColumn = "id",
        entityColumn = "workoutExerciseId"
    )
    val weightProgressions: List<WeightProgressionModel> = emptyList()
)