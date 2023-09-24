package com.example.gym.database.hilt

import android.content.Context
import androidx.room.Room
import com.example.gym.database.TrackerDatabase
import com.example.gym.database.TrackerDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class TrackerDatabaseModule {
    @Provides
    fun provideTrackerDatabaseDao(trackerDatabase: TrackerDatabase): TrackerDatabaseDao {
        return trackerDatabase.trackerDao()
    }
    @Provides
    @Singleton
    fun provideTrackerDatabase(@ApplicationContext appContext: Context): TrackerDatabase {
        return Room.databaseBuilder(
            appContext,
            TrackerDatabase::class.java,
            "tracker_database"
        ).build()
    }
}


//class DatabaseModule {
//    @Provides
//    fun provideChannelDao(appDatabase: AppDatabase): ChannelDao {
//        return appDatabase.channelDao()
//    }
//}

//@Provides
//@Singleton
//fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
//    return Room.databaseBuilder(
//        appContext,
//        AppDatabase::class.java,
//        "RssReader"
//    ).build()
//}