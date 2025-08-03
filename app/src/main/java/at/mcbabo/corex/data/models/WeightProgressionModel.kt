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
            entity = ExerciseModel::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("exerciseId"),
        Index("date")
    ]
)
data class WeightProgressionModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exerciseId: Long,
    val weight: Float,
    val date: Date = Date(),
    val notes: String? = null // optional notes for this specific weight entry
)