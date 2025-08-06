package at.mcbabo.corex.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import at.mcbabo.corex.data.entities.Workout
import at.mcbabo.corex.data.entities.WorkoutSummary
import at.mcbabo.corex.data.entities.WorkoutSummaryRaw
import at.mcbabo.corex.data.models.WorkoutModel
import at.mcbabo.corex.data.models.WorkoutWeekdayModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Dao
interface WorkoutDao {
    // Basic CRUD
    @Upsert
    suspend fun insertWorkout(workout: WorkoutModel): Long

    @Update
    suspend fun updateWorkout(workout: WorkoutModel)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutModel)

    @Insert
    suspend fun insertWorkoutWeekdays(weekdays: List<WorkoutWeekdayModel>)

    @Query("DELETE FROM workout_weekdays WHERE workoutId = :workoutId")
    suspend fun deleteWorkoutWeekdays(workoutId: Long)

    // Simple queries
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): WorkoutModel?

    @Query("SELECT * FROM workouts WHERE isActive = 1 ORDER BY name")
    fun getActiveWorkouts(): Flow<List<WorkoutModel>>

    @Query(
        """
        SELECT DISTINCT w.*
        FROM workouts w
        INNER JOIN workout_weekdays wwd ON w.id = wwd.workoutId
        WHERE w.isActive = 1 AND wwd.weekday = :weekday
        ORDER BY w.name
    """
    )
    suspend fun getWorkoutsByWeekday(weekday: Int): List<WorkoutModel>

    // With relations
    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getFullWorkout(id: Long): Workout?

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    fun observeFullWorkout(id: Long): Flow<Workout?>

    @Transaction
    @Query("SELECT * FROM workouts WHERE isActive = 1 ORDER BY name")
    fun getAllFullWorkouts(): Flow<List<Workout>>

    // Workout summaries - Raw queries (without weekdays populated)
    @Query(
        """
        SELECT w.id, w.name, w.isActive, 
               COUNT(DISTINCT we.id) as exerciseCount
        FROM workouts w
        LEFT JOIN workout_exercises we ON w.id = we.workoutId
        WHERE w.isActive = 1
        GROUP BY w.id, w.name, w.isActive
        ORDER BY w.name
    """
    )
    suspend fun getAllWorkoutSummariesRaw(): List<WorkoutSummaryRaw>

    @Query(
        """
        SELECT w.id, w.name, w.isActive, 
               COUNT(DISTINCT we.id) as exerciseCount
        FROM workouts w
        INNER JOIN workout_weekdays wwd ON w.id = wwd.workoutId
        LEFT JOIN workout_exercises we ON w.id = we.workoutId
        WHERE wwd.weekday = :weekday AND w.isActive = 1
        GROUP BY w.id, w.name, w.isActive
        ORDER BY w.name
    """
    )
    suspend fun getWorkoutSummariesByWeekdayRaw(weekday: Int): List<WorkoutSummaryRaw>

    @Query(
        """
        SELECT w.id, w.name, w.isActive, 
               COUNT(DISTINCT we.id) as exerciseCount
        FROM workouts w
        LEFT JOIN workout_exercises we ON w.id = we.workoutId
        WHERE w.isActive = 1
        GROUP BY w.id, w.name, w.isActive
        ORDER BY w.name
    """
    )
    fun observeAllWorkoutSummariesRaw(): Flow<List<WorkoutSummaryRaw>>

    // Helper query to get weekdays for a specific workout
    @Query("SELECT weekday FROM workout_weekdays WHERE workoutId = :workoutId ORDER BY weekday")
    suspend fun getWeekdaysForWorkout(workoutId: Long): List<Int>

    @Query("SELECT weekday FROM workout_weekdays WHERE workoutId = :workoutId ORDER BY weekday")
    fun observeWeekdaysForWorkout(workoutId: Long): Flow<List<Int>>

    // Workout summaries with weekdays populated
    @Transaction
    suspend fun getAllWorkoutSummaries(): List<WorkoutSummary> {
        val rawSummaries = getAllWorkoutSummariesRaw()
        return rawSummaries.map { raw ->
            val weekdays = getWeekdaysForWorkout(raw.id)
            WorkoutSummary(
                id = raw.id,
                name = raw.name,
                isActive = raw.isActive,
                exerciseCount = raw.exerciseCount,
                weekdays = weekdays
            )
        }
    }

    @Transaction
    suspend fun getWorkoutSummariesByWeekday(weekday: Int): List<WorkoutSummary> {
        val rawSummaries = getWorkoutSummariesByWeekdayRaw(weekday)
        return rawSummaries.map { raw ->
            val weekdays = getWeekdaysForWorkout(raw.id)
            WorkoutSummary(
                id = raw.id,
                name = raw.name,
                isActive = raw.isActive,
                exerciseCount = raw.exerciseCount,
                weekdays = weekdays
            )
        }
    }

    // Flow version that returns WorkoutSummary with weekdays
    fun getWorkoutSummaries(): Flow<List<WorkoutSummary>> = flow {
        observeAllWorkoutSummariesRaw().collect { rawSummaries ->
            val summaries = rawSummaries.map { raw ->
                val weekdays = getWeekdaysForWorkout(raw.id)
                WorkoutSummary(
                    id = raw.id,
                    name = raw.name,
                    isActive = raw.isActive,
                    exerciseCount = raw.exerciseCount,
                    weekdays = weekdays
                )
            }
            emit(summaries)
        }
    }

    // Convenience methods
    @Transaction
    suspend fun insertWorkoutWithWeekdays(workout: WorkoutModel, weekdays: List<Int>): Long {
        val workoutId = insertWorkout(workout)
        val weekdayModels = weekdays.map { weekday ->
            WorkoutWeekdayModel(workoutId = workoutId, weekday = weekday)
        }
        insertWorkoutWeekdays(weekdayModels)
        return workoutId
    }

    @Transaction
    suspend fun updateWorkoutWeekdays(workoutId: Long, weekdays: List<Int>) {
        deleteWorkoutWeekdays(workoutId)
        val weekdayModels = weekdays.map { weekday ->
            WorkoutWeekdayModel(workoutId = workoutId, weekday = weekday)
        }
        insertWorkoutWeekdays(weekdayModels)
    }

    @Transaction
    suspend fun upsertWorkoutWithWeekdays(workout: WorkoutModel, weekdays: List<Int>): Long {
        val workoutId = insertWorkout(workout)
        updateWorkoutWeekdays(workoutId, weekdays)
        return workoutId
    }
}
