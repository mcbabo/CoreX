package at.mcbabo.corex.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.DayOfWeek

@Entity(tableName = "workouts")
data class WorkoutModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isActive: Boolean = true
)

@Entity(
    tableName = "workout_weekdays",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutModel::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("workoutId"),
        Index("weekday"),
        Index(value = ["workoutId", "weekday"], unique = true) // Prevent duplicate weekday assignments
    ]
)
data class WorkoutWeekdayModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workoutId: Long,
    val weekday: Int
)

fun List<WorkoutWeekdayModel>.toDayOfWeekSet(): Set<DayOfWeek> {
    return map { DayOfWeek.of(it.weekday % 7) }.toSet()
}

fun Set<DayOfWeek>.toWeekdayInts(): List<Int> {
    return map { it.value }.sorted()
}
