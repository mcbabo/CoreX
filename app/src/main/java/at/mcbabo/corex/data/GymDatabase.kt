package at.mcbabo.corex.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import at.mcbabo.corex.data.dao.ExerciseDao
import at.mcbabo.corex.data.dao.WeightProgressionDao
import at.mcbabo.corex.data.dao.WorkoutDao
import at.mcbabo.corex.data.dao.WorkoutExerciseDao
import at.mcbabo.corex.data.models.ExerciseModel
import at.mcbabo.corex.data.models.WeightProgressionModel
import at.mcbabo.corex.data.models.WorkoutExerciseModel
import at.mcbabo.corex.data.models.WorkoutModel
import at.mcbabo.corex.data.models.WorkoutWeekdayModel

@Database(
    entities = [
        WorkoutModel::class,
        ExerciseModel::class,
        WorkoutExerciseModel::class,
        WeightProgressionModel::class,
        WorkoutWeekdayModel::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class GymDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    abstract fun exerciseDao(): ExerciseDao

    abstract fun workoutExerciseDao(): WorkoutExerciseDao

    abstract fun weightProgressionDao(): WeightProgressionDao

    @Suppress("unused")
    companion object {
        @Suppress("unused")
        @Volatile
        private var instance: GymDatabase? = null
    }

    // Callback for database creation
    @Suppress("unused")
    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
        }
    }
}
