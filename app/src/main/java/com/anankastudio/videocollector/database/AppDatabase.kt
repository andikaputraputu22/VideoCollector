package com.anankastudio.videocollector.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anankastudio.videocollector.models.room.DetailVideo

@Database(entities = [DetailVideo::class], version = 1, exportSchema = false)
@TypeConverters(VideoFileConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun detailVideoDao(): DetailVideoDao

    companion object {
        fun getDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "app_database"
            ).build()
        }
    }
}