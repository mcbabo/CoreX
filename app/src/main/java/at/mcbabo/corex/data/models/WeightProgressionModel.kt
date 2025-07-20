package at.mcbabo.corex.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "weight_progressions",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExerciseModel::class,
            parentColumns = ["id"],
            childColumns = ["workoutExerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("workoutExerciseId"),
        Index("date")
    ]
)
data class WeightProgressionModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workoutExerciseId: Long,
    val weight: Float,
    val date: Date = Date(),
    val notes: String? = null // optional notes for this specific weight entry
)