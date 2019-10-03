package com.helge.moviebook.repository.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

private const val DB_NAME = "database"

@Database(entities = [RecentEntity::class], version = 1)
@TypeConverters(TypeTransmogrifier::class)
abstract class RecentDatabase : RoomDatabase() {
    abstract fun recentStore(): RecentEntity.Store

    companion object {
        fun newInstance(context: Context) =
            Room.databaseBuilder(context, RecentDatabase::class.java, DB_NAME).build()

        /**
         * for unit tests
         */
        fun newTestInstance(context: Context) =
            Room.inMemoryDatabaseBuilder(context, RecentDatabase::class.java).build()
    }
}
