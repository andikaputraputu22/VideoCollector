package com.anankastudio.videocollector.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anankastudio.videocollector.models.room.WidgetVideo

@Dao
interface WidgetVideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWidgetVideo(widgetVideos: List<WidgetVideo>)

    @Query("SELECT * FROM widget_video ORDER BY timestamp ASC")
    suspend fun getAllWidgetVideo(): List<WidgetVideo>

    @Query("DELETE FROM widget_video")
    suspend fun deleteAllWidgetVideo()
}