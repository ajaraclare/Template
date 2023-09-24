package com.example.gym.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gym.database.converters.DateConverters
import com.example.gym.database.converters.ExerciseConverters
import com.example.gym.database.converters.IntegerConverters
import com.example.gym.database.converters.StringConverters

@Database(entities = [ExerciseItem::class, RoutineItem::class, SessionItem::class], version = 3, exportSchema = false)
@TypeConverters(StringConverters::class, ExerciseConverters::class, DateConverters::class, IntegerConverters::class)
abstract class TrackerDatabase : RoomDatabase() {
    abstract fun trackerDao(): TrackerDatabaseDao

    companion object {
        private var INSTANCE: TrackerDatabase? = null

        fun getInstance(context: Context): TrackerDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TrackerDatabase::class.java,
                        "tracker_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

//val MIGRATION_1_2 = object : Migration(1, 2) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        // Delete the old table
//        database.execSQL("DROP TABLE IF EXISTS tracker_database")
//    }
//}