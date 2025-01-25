package com.anankastudio.videocollector.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anankastudio.videocollector.models.room.FavoriteVideo

@Dao
interface FavoriteVideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteVideo(favoriteVideo: FavoriteVideo)

    @Query("SELECT * FROM favorite_video ORDER BY timestamp DESC")
    suspend fun getAllFavoriteVideo(): List<FavoriteVideo>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_video WHERE id = :id)")
    suspend fun isVideoExists(id: Long): Boolean

    @Query("DELETE FROM favorite_video WHERE id = :id")
    suspend fun deleteFavoriteVideo(id: Long)

    @Query("DELETE FROM favorite_video")
    suspend fun deleteAllFavoriteVideo()
}