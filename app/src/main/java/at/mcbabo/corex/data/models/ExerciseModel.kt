package at.mcbabo.corex.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val muscleGroup: String,
    val isCustom: Boolean = false,
    val isBodyWeight: Boolean = false
)