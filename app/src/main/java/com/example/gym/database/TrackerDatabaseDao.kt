package com.example.gym.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackerDatabaseDao {

    companion object {
        private const val EXERCISE_TABLE = "exercises"
        private const val ROUTINE_TABLE = "routines"
        private const val SESSION_TABLE = "sessions"
    }

    @Query("SELECT * from $EXERCISE_TABLE")
    fun getAllExercises(): Flow<List<ExerciseItem>>

    @Insert
    suspend fun insert(item: ExerciseItem)

    @Update
    suspend fun update(item: ExerciseItem)

    @Delete
    suspend fun delete(item: ExerciseItem)

    @Query("SELECT * from $ROUTINE_TABLE")
    fun getAllRoutines(): Flow<List<RoutineItem>>

    @Insert
    suspend fun insert(item: RoutineItem)

    @Update
    suspend fun update(item: RoutineItem)

    @Delete
    suspend fun delete(item: RoutineItem)

//    @Query("SELECT * FROM $ROUTINE_TABLE WHERE id=:id")
//    fun getRoutineById(id: String): RoutineItem
    @Query("SELECT * FROM $ROUTINE_TABLE WHERE routine_name=:name")
    suspend fun getRoutineByName(name: String): RoutineItem?

    @Query("SELECT * FROM $ROUTINE_TABLE WHERE id=:id")
    suspend fun getRoutineById(id: Long): RoutineItem?

    @Query("SELECT * FROM $EXERCISE_TABLE WHERE name=:name")
    suspend fun getExerciseByName(name: String): ExerciseItem?

    @Insert
    suspend fun insert(item: SessionItem)

    @Query("SELECT * FROM $SESSION_TABLE ORDER BY date_created DESC LIMIT 5")
    fun getMostRecentSessions(): Flow<List<SessionItem>>

    @Query("SELECT * FROM $SESSION_TABLE WHERE date_created >= date('now', '-30 day')")
    fun getSessionsWithinMonth(): Flow<List<SessionItem>>
}