package com.example.gym.database

import com.example.gym.MuscleGroup

//private val mockExercises = listOf(
//    Exercise(generateUniqueId(), "Squats", listOf("Quads", "Hamstrings")),
//    Exercise(generateUniqueId(), "Leg Extensions", listOf("Quads")),
//    Exercise(generateUniqueId(), "Bulgarian Squat", listOf("Glutes", "Quads")),
//    Exercise(generateUniqueId(), "Hamstring Curls", listOf("Hamstrings")),
//    Exercise(generateUniqueId(), "Calve raises", listOf("Calves"))
//)
//
//private val mockExercises = listOf(
//    Exercise( "Squats", listOf("Quads", "Hamstrings")),
//    Exercise("Leg Extensions", listOf("Quads")),
//    Exercise("Bulgarian Squat", listOf("Glutes", "Quads")),
//    Exercise("Hamstring Curls", listOf("Hamstrings")),
//    Exercise("Calve raises", listOf("Calves"))
//)
//
//
//fun getSampleExercises(): List<Exercise> {
//    return mockExercises
//}

//private val mockRoutine = Routine(
//    generateUniqueId(),
//    "Leg Day",
//    mockExercises,
//    listOf(
//        "Quads", "Hamstrings", "Glutes", "Calves"
//    )
//)
//private val mockRoutines = listOf(mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine, mockRoutine)

//fun getSampleRoutine(): Routine {
//    return mockRoutine
//}
//
//fun getSampleRoutines(): List<Routine> {
//    return mockRoutines
//}

enum class MuscleType {
    BACK,
    CHEST,
    ARMS,
    SHOULDERS,
    LEGS,
}



//data class Muscle (
//    val value: String,
//    val type: MuscleType,
//)

//private val sampleMuscles = listOf(
//    listOf(Muscle("Lats", MuscleType.BACK), Muscle("Rhomboids", MuscleType.BACK),
//        Muscle("Traps", MuscleType.BACK), Muscle("Spinal Erector", MuscleType.BACK)),
//    listOf(Muscle("Front Delts", MuscleType.SHOULDERS), Muscle("Side Delts", MuscleType.SHOULDERS),
//        Muscle("Rear Delts", MuscleType.SHOULDERS)),
//    listOf(Muscle("Upper Chest", MuscleType.CHEST), Muscle("Lower Chest", MuscleType.CHEST)),
//    listOf(Muscle("Forearms", MuscleType.ARMS), Muscle("Triceps", MuscleType.ARMS),
//        Muscle("Biceps", MuscleType.ARMS)),
//    listOf(Muscle("Quads", MuscleType.LEGS), Muscle("Hamstrings", MuscleType.LEGS),
//        Muscle("Glutes", MuscleType.LEGS), Muscle("Calves", MuscleType.LEGS))
//)

private val sampleMuscles = listOf(
    MuscleGroup(listOf("Lats", "Rhomboids", "Traps", "Spinal Erector"), MuscleType.BACK),
    MuscleGroup(listOf("Quads", "Hamstrings", "Glutes", "Calves"), MuscleType.LEGS),
    MuscleGroup(listOf("Front Delts","Side Delts", "Rear Delts"), MuscleType.SHOULDERS),
    MuscleGroup(listOf("Forearms", "Triceps", "Biceps"), MuscleType.ARMS),
    MuscleGroup(listOf("Upper Chest", "Lower Chest"), MuscleType.CHEST),
)

//private val maxNumMuscles = 4

fun getSampleMuscles(): List<MuscleGroup> {
    return sampleMuscles
}


//fun getMaxNumMuscles(): Int {
//    return maxNumMuscles
//}
//
//fun generateUniqueId(): Long {
//    val uuid = UUID.randomUUID()
//    return uuid.mostSignificantBits and Long.MAX_VALUE
//}