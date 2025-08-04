package at.mcbabo.corex.data

import androidx.sqlite.db.SupportSQLiteDatabase

fun insertDefaultExercises(db: SupportSQLiteDatabase) {
    // Default exercises using raw SQL INSERT statements
    val defaultExercises =
        listOf(
            // Chest
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Bench Press', 'Chest', 'Barbell bench press', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Incline Bench Press', 'Chest', 'Incline barbell bench press', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Decline Bench Press', 'Chest', 'Decline barbell bench press', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Push-Up', 'Chest', 'Standard push-up', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Dumbbell Flyes', 'Chest', 'Chest flyes using dumbbells', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Cable Crossover', 'Chest', 'Cable chest crossover', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Chest Press Machine', 'Chest', 'Machine-based chest press', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Pec Deck', 'Chest', 'Pec deck machine flyes', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Dips (Chest)', 'Chest', 'Chest-focused dips', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Incline Dumbbell Press', 'Chest', 'Incline press using dumbbells', 0, 0)",

            // Back
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Pull-Up', 'Back', 'Standard pull-up', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Chin-Up', 'Back', 'Underhand grip pull-up', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Lat Pulldown', 'Back', 'Lat pulldown machine', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Barbell Row', 'Back', 'Bent-over barbell row', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('T-Bar Row', 'Back', 'T-bar row with chest support', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Seated Cable Row', 'Back', 'Cable row with neutral grip', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Dumbbell Row', 'Back', 'Single-arm dumbbell row', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Inverted Row', 'Back', 'Bodyweight inverted row', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Deadlift', 'Back', 'Barbell deadlift', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Rack Pulls', 'Back', 'Partial deadlift from rack', 0, 0)",

            // Biceps
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Barbell Curl', 'Biceps', 'Standing barbell bicep curl', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Dumbbell Curl', 'Biceps', 'Standing dumbbell bicep curl', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Hammer Curl', 'Biceps', 'Neutral grip curl', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Preacher Curl', 'Biceps', 'EZ bar preacher curl', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Cable Curl', 'Biceps', 'Curl using cable machine', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Concentration Curl', 'Biceps', 'Seated one-arm dumbbell curl', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Incline Curl', 'Biceps', 'Dumbbell curl on incline bench', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Zottman Curl', 'Biceps', 'Curl with twist at top', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Spider Curl', 'Biceps', 'Curl on preacher bench facing down', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Chin-Up (Biceps)', 'Biceps', 'Chin-up focusing on biceps', 0, 1)",

            // Triceps
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Triceps Pushdown', 'Triceps', 'Cable pushdown with straight bar', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Skull Crushers', 'Triceps', 'Lying triceps extensions', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Close-Grip Bench Press', 'Triceps', 'Barbell bench press with close grip', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Overhead Triceps Extension', 'Triceps', 'Dumbbell triceps extension', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Triceps Kickback', 'Triceps', 'Dumbbell triceps kickback', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Dips (Triceps)', 'Triceps', 'Triceps-focused dips', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Rope Pushdown', 'Triceps', 'Cable pushdown with rope', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Bench Dips', 'Triceps', 'Bodyweight dips on bench', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Machine Triceps Extension', 'Triceps', 'Seated triceps extension machine', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Single-Arm Cable Extension', 'Triceps', 'Cable triceps extension one arm', 0, 0)",

            // Legs
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Squat', 'Legs', 'Barbell back squat', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Front Squat', 'Legs', 'Barbell front squat', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Lunges', 'Legs', 'Bodyweight or dumbbell lunges', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Leg Press', 'Legs', 'Leg press machine', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Bulgarian Split Squat', 'Legs', 'Split squat with rear foot elevated', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Step-Up', 'Legs', 'Step-up onto bench with dumbbells', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Leg Extension', 'Legs', 'Machine leg extension', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Leg Curl', 'Legs', 'Machine hamstring curl', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Romanian Deadlift', 'Legs', 'Barbell RDL', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Glute Bridge', 'Legs', 'Bodyweight glute bridge', 0, 1)",

            // Core
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Plank', 'Core', 'Isometric plank hold', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Crunches', 'Core', 'Standard floor crunches', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Sit-Ups', 'Core', 'Traditional sit-ups', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Russian Twists', 'Core', 'Twisting motion with or without weight', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Hanging Leg Raises', 'Core', 'Leg raises from pull-up bar', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Cable Woodchopper', 'Core', 'Twisting motion with cable', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Bicycle Crunches', 'Core', 'Alternating elbow-to-knee crunches', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Ab Wheel Rollout', 'Core', 'Roll forward with ab wheel', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Side Plank', 'Core', 'Isometric hold on side', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Decline Sit-Up', 'Core', 'Sit-ups on decline bench', 0, 1)",

            // Shoulders
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Shoulder Press', 'Shoulders', 'Seated or standing overhead press', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Dumbbell Lateral Raise', 'Shoulders', 'Side shoulder raise with dumbbells', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Front Raise', 'Shoulders', 'Raise weights in front', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Rear Delt Flyes', 'Shoulders', 'Reverse fly for rear delts', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Arnold Press', 'Shoulders', 'Overhead press with twist', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Barbell Overhead Press', 'Shoulders', 'Standing barbell overhead press', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Cable Lateral Raise', 'Shoulders', 'Lateral raise with cable machine', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Face Pulls', 'Shoulders', 'Cable rear delt pull', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Upright Row', 'Shoulders', 'Barbell or dumbbell upright row', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Handstand Push-Up', 'Shoulders', 'Inverted bodyweight press', 0, 1)"
        )

    // Execute each INSERT statement
    defaultExercises.forEach { sql ->
        db.execSQL(sql)
    }
}
