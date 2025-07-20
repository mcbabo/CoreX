package at.mcbabo.corex.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val weekday: String, // e.g., "Monday", "Tuesday", etc.
    val isActive: Boolean = true // allows users to disable workouts without deleting
)