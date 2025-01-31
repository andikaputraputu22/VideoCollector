package com.anankastudio.videocollector.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anankastudio.videocollector.models.room.DetailVideo
import com.anankastudio.videocollector.models.room.FavoriteVideo
import com.anankastudio.videocollector.models.room.SearchHistory

@Database(entities = [DetailVideo::class, FavoriteVideo::class, SearchHistory::class], version = 1, exportSchema = false)
@TypeConverters(VideoFileConverter::class, VideoPictureConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun detailVideoDao(): DetailVideoDao
    abstract fun favoriteVideoDao(): FavoriteVideoDao
    abstract fun searchHistoryDao(): SearchHistoryDao

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