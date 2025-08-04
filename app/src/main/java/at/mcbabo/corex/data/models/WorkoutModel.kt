package at.mcbabo.corex.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    // e.g., 0: "Monday", 1: "Tuesday", etc.
    val weekday: Int,
    val isActive: Boolean = true
)
