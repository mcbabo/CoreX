package at.mcbabo.corex.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutModel::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseModel::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("workoutId"),
        Index("exerciseId")
    ]
)
data class WorkoutExerciseModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workoutId: Long,
    val exerciseId: Long,
    val orderIndex: Int, // for ordering exercises within a workout
    val targetWeight: Float? = null, // user's target weight for this exercise
    val targetReps: Int? = null, // user's target reps for this exercise
    val targetSets: Int? = null, // user's target sets for this exercise
    val notes: String? = null, // user notes for this specific exercise in this workout
    val isCompleted: Boolean = false // track if exercise was completed in current session
)