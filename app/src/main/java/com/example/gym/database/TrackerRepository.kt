package com.example.gym.database

import com.example.gym.Exercise
import com.example.gym.Routine
import com.example.gym.SessionEntry
import javax.inject.Inject

class TrackerRepository @Inject constructor(
    private val trackerDatabaseDao: TrackerDatabaseDao
) {
    val readAllExerciseData = trackerDatabaseDao.getAllExercises()
    val readAllRoutineData = trackerDatabaseDao.getAllRoutines()
    val readMostRecentSessionEntries = trackerDatabaseDao.getMostRecentSessions()
    val readSessionsWithinMonth = trackerDatabaseDao.getSessionsWithinMonth()

    suspend fun addExercise(item: Exercise) {
        trackerDatabaseDao.insert(
            ExerciseItem(
                name = item.name,
                muscles = item.muscleGroups
            )
        )
    }

    suspend fun updateExercise(item: Exercise) {
        trackerDatabaseDao.update(
            ExerciseItem(
                name = item.name,
                muscles = item.muscleGroups
            )
        )
    }

    suspend fun deleteExercise(item: Exercise) {
        trackerDatabaseDao.delete(
            ExerciseItem(
                name = item.name,
                muscles = item.muscleGroups
            )
        )
    }

    suspend fun addRoutine(item: Routine) {
        trackerDatabaseDao.insert(
            RoutineItem(
                id = item.id,
                name = item.name,
                exercises = item.exercises,
                muscles = item.muscleGroups
            )
        )
    }

    suspend fun updateRoutine(item: Routine) {
        trackerDatabaseDao.update(
            RoutineItem(
                id = item.id,
                name = item.name,
                exercises = item.exercises,
                muscles = item.muscleGroups
            )
        )
    }

    suspend fun deleteRoutine(item: Routine) {
        trackerDatabaseDao.delete(
            RoutineItem(
                id = item.id,
                name = item.name,
                exercises = item.exercises,
                muscles = item.muscleGroups
            )
        )
    }

//    fun readRoutineById(id: String): RoutineItem {
//        return trackerDatabaseDao.getRoutineById(id)
//    }
    suspend fun readRoutineByName(name: String): RoutineItem? {
        return trackerDatabaseDao.getRoutineByName(name)
    }

    suspend fun readRoutineById(id: Long): RoutineItem? {
        return trackerDatabaseDao.getRoutineById(id)
    }

    suspend fun readExerciseByName(name: String): ExerciseItem? {
        return trackerDatabaseDao.getExerciseByName(name)
    }

    suspend fun addSessionEntry(entry: SessionEntry) {
        trackerDatabaseDao.insert(
            SessionItem(
                routineName = entry.routineName,
                repCounts = entry.repCounts,
                dateCreated = entry.dateCreated.value,
            )
        )
    }

//    fun readMostRecentSessionEntries(): Flow<List<SessionItem>> {
//        return trackerDatabaseDao.getMostRecentSessions()
//    }
}