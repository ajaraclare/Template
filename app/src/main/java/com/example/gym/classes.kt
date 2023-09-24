package com.example.gym

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.gym.database.MuscleType
import com.example.gym.navigation.Screen
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.getField
import java.time.LocalDateTime


data class Routine(
    var id: Long,
    var name: String,
    var exercises: List<Exercise>,
    var muscleGroups: List<String>
) {
    companion object {
        fun fromDocumentSnapshot(documentSnapshot: DocumentSnapshot): Routine {
            val name = documentSnapshot.getString("name") ?: ""
            val id = documentSnapshot.getLong("id") ?: (-1).toLong()
            val stringList = documentSnapshot.get("muscleGroups")
//            Log.d("Routine", stringList?.toString().toString())
            val muscleGroups = if (stringList is ArrayList<*>) {
                val groups = mutableListOf<String>()
                stringList.forEach {
                    groups.add(it.toString())
                }
                groups
            } else {
                emptyList()
            }
            val exerciseList = documentSnapshot.get("exercises")
            val exercises = if (exerciseList is ArrayList<*>) {
                val list = mutableListOf<Exercise>()
                exerciseList.forEach {
                    val data = it as? HashMap<*, *>
                    val exerciseName = data?.get("name") as String
                    val exerciseStringList = data.get("muscleGroups") as ArrayList<*>
                    val exerciseMuscleGroups = mutableListOf<String>()
                    exerciseStringList.forEach {
                        exerciseMuscleGroups.add(it.toString())
                    }
//                    Log.d("Routine name", name)
//                    Log.d("Routine groups", muscleGroups.toString())
//                    Log.d("Routine class", it.javaClass.toString())
                    list.add(Exercise(
                        name = exerciseName,
                        muscleGroups = muscleGroups
                    ))
                }
                list
            } else {
                emptyList()
            }
            // Extract other fields from the DocumentSnapshot
            return Routine(name = name, muscleGroups = muscleGroups, id = id, exercises = exercises)
        }
    }
}
data class Exercise(
//    var id: Long,
    var name: String = "",
    var muscleGroups: List<String> = emptyList(),
)

data class MuscleGroup (
    val list: List<String>,
    val type: MuscleType,
)

data class NavIcon(
    @DrawableRes val icon: Int,
    @StringRes val info: Int,
    val screenVal: Screen,
)

data class SessionEntry(
    val routineName: String,
    val repCounts: List<Int>,
    val dateCreated: CustomDateTime,
) {
    companion object {
        fun fromDocumentSnapshot(documentSnapshot: DocumentSnapshot): SessionEntry {
            val name = documentSnapshot.getString("routineName") ?: ""
            val dateCreated = documentSnapshot.getField("dateCreated") ?: CustomDateTime()
            val reps = documentSnapshot.get("repCounts")
//            Log.d("Session Entry", reps?.toString().toString())
            val repCounts = if (reps is ArrayList<*>) {
                val counts = mutableListOf<Int>()

                reps.forEach {
                    counts.add(it.toString().toInt())
                }
                counts
            } else {
                emptyList()
            }
            // Extract other fields from the DocumentSnapshot
            return SessionEntry(routineName = name, dateCreated = dateCreated, repCounts = repCounts)
        }
    }
}

data class SessionStatistic(
    val routineName: String,
    val exerciseStatistics: MutableList<ExerciseStatistic>,
)

data class ExerciseStatistic(
    val exerciseName: String,
    val repCounts: MutableList<Float>,
)

data class CustomDateTime(val value: LocalDateTime) {
    constructor() : this(LocalDateTime.now())
}
