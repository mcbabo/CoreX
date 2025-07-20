package at.mcbabo.corex.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import at.mcbabo.corex.data.dao.ExerciseDao
import at.mcbabo.corex.data.dao.WeightProgressionDao
import at.mcbabo.corex.data.dao.WorkoutDao
import at.mcbabo.corex.data.dao.WorkoutExerciseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GymDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            GymDatabase::class.java,
            "fitness_database"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Pre-populate with default exercises using raw SQL
                    insertDefaultExercises(db)
                }
            })
            .build()
    }

    @Provides
    fun provideWorkoutDao(database: GymDatabase): WorkoutDao = database.workoutDao()

    @Provides
    fun provideExerciseDao(database: GymDatabase): ExerciseDao = database.exerciseDao()

    @Provides
    fun provideWorkoutExerciseDao(database: GymDatabase): WorkoutExerciseDao =
        database.workoutExerciseDao()

    @Provides
    fun provideWeightProgressionDao(database: GymDatabase): WeightProgressionDao =
        database.weightProgressionDao()
}