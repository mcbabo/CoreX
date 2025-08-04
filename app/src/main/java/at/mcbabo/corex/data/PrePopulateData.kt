package at.mcbabo.corex.data

import androidx.sqlite.db.SupportSQLiteDatabase

fun insertDefaultExercises(db: SupportSQLiteDatabase) {
    // Default exercises using raw SQL INSERT statements
    val defaultExercises =
        listOf(
            // Chest
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Bench Press', 'Chest', 'Barbell bench press', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Dumbbell Press', 'Chest', 'Dumbbell bench press', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Push-ups', 'Chest', 'Bodyweight push-ups', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Incline Bench Press', 'Chest', 'Incline barbell press', 0, 0)",
            // Back
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Deadlift', 'Back', 'Conventional deadlift', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Pull-ups', 'Back', 'Bodyweight pull-ups', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Barbell Rows', 'Back', 'Bent-over barbell rows', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Lat Pulldown', 'Back', 'Cable lat pulldown', 0, 0)",
            // Legs
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Squats', 'Legs', 'Barbell back squats', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Leg Press', 'Legs', 'Leg press machine', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Lunges', 'Legs', 'Walking lunges', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Romanian Deadlift', 'Legs', 'RDL for hamstrings', 0, 0)",
            // Shoulders
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Overhead Press', 'Shoulders', 'Standing barbell press', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Dumbbell Shoulder Press', 'Shoulders', 'Seated dumbbell press', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Lateral Raises', 'Shoulders', 'Dumbbell lateral raises', 0, 0)",
            // Arms
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Bicep Curls', 'Arms', 'Barbell bicep curls', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Tricep Dips', 'Arms', 'Bodyweight tricep dips', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Hammer Curls', 'Arms', 'Dumbbell hammer curls', 0, 0)",
            // Core
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Plank', 'Core', 'Standard plank hold', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Crunches', 'Core', 'Abdominal crunches', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Russian Twists', 'Core', 'Seated Russian twists', 0, 1)"
        )

    // Execute each INSERT statement
    defaultExercises.forEach { sql ->
        db.execSQL(sql)
    }
}
