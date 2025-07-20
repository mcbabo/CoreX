package at.mcbabo.corex.data

import at.mcbabo.corex.data.repositories.ExerciseRepository
import at.mcbabo.corex.data.repositories.ExerciseRepositoryImpl
import at.mcbabo.corex.data.repositories.ProgressRepository
import at.mcbabo.corex.data.repositories.ProgressRepositoryImpl
import at.mcbabo.corex.data.repositories.WorkoutRepository
import at.mcbabo.corex.data.repositories.WorkoutRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindWorkoutRepository(
        workoutRepositoryImpl: WorkoutRepositoryImpl
    ): WorkoutRepository

    @Binds
    abstract fun bindExerciseRepository(
        exerciseRepositoryImpl: ExerciseRepositoryImpl
    ): ExerciseRepository

    @Binds
    abstract fun bindProgressRepository(
        progressRepositoryImpl: ProgressRepositoryImpl
    ): ProgressRepository
}