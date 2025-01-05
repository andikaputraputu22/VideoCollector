package com.anankastudio.videocollector.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anankastudio.videocollector.models.room.DetailVideo

@Dao
interface DetailVideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(detailVideo: DetailVideo)

    @Query("DELETE FROM detail_video WHERE id = :id")
    suspend fun deleteVideo(id: Long)

    @Query("SELECT * FROM detail_video WHERE id = :id")
    suspend fun getVideoById(id: Long): DetailVideo?
}