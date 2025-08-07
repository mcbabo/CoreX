package at.mcbabo.corex.data

import androidx.sqlite.db.SupportSQLiteDatabase

fun insertDefaultExercises(db: SupportSQLiteDatabase) {
    // Default exercises using raw SQL INSERT statements
    val defaultExercises =
        listOf(
            // Chest
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Bench Press', 'Chest', 'Barbell bench press targeting chest, shoulders, and triceps for upper body strength and mass.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Incline Bench Press', 'Chest', 'Targets upper chest using a barbell at an incline bench.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Dumbbell Flyes', 'Chest', 'Isolation movement for chest using dumbbells with a wide arc.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Push-Up', 'Chest', 'Classic bodyweight chest exercise involving horizontal pressing.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Cable Crossover', 'Chest', 'Cable isolation for chest emphasizing peak contraction.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Chest Dip', 'Chest', 'Bodyweight dip leaning forward to emphasize chest over triceps.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Incline Dumbbell Press', 'Chest', 'Dumbbell press at an incline targeting the upper chest.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Decline Bench Press', 'Chest', 'Barbell press at decline angle for lower chest focus.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Machine Chest Press', 'Chest', 'Guided chest pressing on a machine for controlled reps.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Incline Cable Fly', 'Chest', 'Cable chest isolation on an incline bench for upper pecs.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Pec Deck Machine', 'Chest', 'Machine-based chest fly for isolation and contraction.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Incline Bench', 'Chest', 'Training of the upper chest muscles.', 0, 0)",

            // Back
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Pull-Up', 'Back', 'Bodyweight exercise building lats and upper back strength.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Deadlift', 'Back', 'Compound barbell lift hitting entire posterior chain.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Barbell Row', 'Back', 'Bent-over barbell row for mid and upper back.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Lat Pulldown', 'Back', 'Cable machine pulling movement targeting lats.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Seated Cable Row', 'Back', 'Cable rowing motion focusing on back thickness.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('T-Bar Row', 'Back', 'Row variation using T-bar for mid-back activation.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Chin-Up', 'Back', 'Bodyweight vertical pull focusing more on biceps and upper back.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Inverted Row', 'Back', 'Bodyweight horizontal row under bar for upper back.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Dumbbell Row', 'Back', 'Single-arm row targeting lats and scapular movement.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Trap Bar Deadlift', 'Back', 'Deadlift variation using trap bar for lower back safety.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Face Pull', 'Back', 'Cable pulling motion hitting rear delts and upper traps.', 0, 0)",

            // Biceps
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Barbell Curl', 'Biceps', 'Classic barbell curl for biceps size and strength.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Dumbbell Curl', 'Biceps', 'Alternating or simultaneous curls for biceps isolation.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Preacher Curl', 'Biceps', 'Strict curl on preacher bench for bicep peak.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Hammer Curl', 'Biceps', 'Neutral grip dumbbell curl hitting brachialis and biceps.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Concentration Curl', 'Biceps', 'Seated curl isolating biceps for peak contraction.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Cable Curl', 'Biceps', 'Constant tension cable curl for full bicep activation.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('EZ Bar Curl', 'Biceps', 'Curl with EZ bar for wrist-friendly biceps training.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Chin-Up', 'Biceps', 'Bodyweight exercise targeting biceps and upper back.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Spider Curl', 'Biceps', 'Curl on incline bench for continuous biceps tension.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Zottman Curl', 'Biceps', 'Curl with supination and pronation to target all elbow flexors.', 0, 0)",

            // Triceps
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Triceps Pushdown', 'Triceps', 'Cable pushdown focusing on triceps isolation.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Overhead Triceps Extension', 'Triceps', 'Overhead dumbbell extension isolating long head of triceps.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Close-Grip Bench Press', 'Triceps', 'Bench press with narrow grip for triceps overload.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Dips', 'Triceps', 'Bodyweight or weighted dip to strengthen triceps.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Skull Crusher', 'Triceps', 'Lying barbell extension targeting all triceps heads.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Kickbacks', 'Triceps', 'Triceps isolation using dumbbell in bent-over stance.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Cable Overhead Extension', 'Triceps', 'Cable triceps isolation with overhead pull.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Diamond Push-Up', 'Triceps', 'Bodyweight push-up variation emphasizing triceps.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('EZ Bar Skullcrusher', 'Triceps', 'Wrist-friendly lying extension for triceps.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Triceps Dip Machine', 'Triceps', 'Machine-assisted dip isolating triceps.', 0, 0)",

            // Legs
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Back Squat', 'Legs', 'Barbell squat developing quads, glutes, and hamstrings.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Front Squat', 'Legs', 'Barbell squat variation emphasizing quads.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Leg Press', 'Legs', 'Machine press developing quads and glutes safely.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Walking Lunge', 'Legs', 'Dynamic lunge working all leg muscles and stability.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Romanian Deadlift', 'Legs', 'Hamstring-dominant hip hinge with barbell.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Bulgarian Split Squat', 'Legs', 'Single-leg squat emphasizing balance and leg strength.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Leg Curl', 'Legs', 'Hamstring isolation on prone or seated machine.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Leg Extension', 'Legs', 'Quad isolation using seated machine.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Calf Raise', 'Legs', 'Standing or seated movement to build calf muscles.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Step-Up', 'Legs', 'Step onto elevated surface for unilateral leg development.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Bodyweight Squat', 'Legs', 'Basic lower body exercise without weights.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Glute Bridge', 'Legs', 'Hip thrust movement targeting glutes and hamstrings.', 0, 1)",

            // Core
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Plank', 'Core', 'Isometric hold strengthening entire core region.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Crunch', 'Core', 'Abdominal curling movement for upper abs.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Russian Twist', 'Core', 'Rotational movement for obliques and abs.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Hanging Leg Raise', 'Core', 'Leg lift from bar for lower abs.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Bicycle Crunch', 'Core', 'Dynamic crunching hitting obliques and abs.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Cable Woodchopper', 'Core', 'Rotational core movement using cable machine.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Side Plank', 'Core', 'Isometric hold targeting obliques.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Ab Wheel Rollout', 'Core', 'Advanced core extension with ab wheel.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Mountain Climbers', 'Core', 'Dynamic plank variation engaging abs and cardio.', 0, 1)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Toe Touches', 'Core', 'Floor-based movement targeting upper and lower abs.', 0, 1)",

            // Shoulders
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Overhead Press', 'Shoulders', 'Barbell press overhead to build shoulder and triceps strength.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Dumbbell Shoulder Press', 'Shoulders', 'Seated or standing press targeting delts.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Lateral Raise', 'Shoulders', 'Dumbbell raise for side deltoid development.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Front Raise', 'Shoulders', 'Raises dumbbells forward for anterior delts.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Rear Delt Fly', 'Shoulders', 'Bent-over dumbbell fly for posterior delts.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Arnold Press', 'Shoulders', 'Rotational dumbbell press engaging full delts.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Upright Row', 'Shoulders', 'Barbell or cable pull targeting delts and traps.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Face Pull', 'Shoulders', 'Cable pull to strengthen rear delts and traps.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Push Press', 'Shoulders', 'Explosive barbell press involving legs and shoulders.', 0, 0)",
            "INSERT INTO exercises (name, muscleGroup, description, isCustom, isBodyWeight) VALUES ('Shoulder Tap', 'Shoulders', 'Plank variation for shoulder and core stability.', 0, 1)",
        )

    // Execute each INSERT statement
    defaultExercises.forEach { sql ->
        db.execSQL(sql)
    }
}
