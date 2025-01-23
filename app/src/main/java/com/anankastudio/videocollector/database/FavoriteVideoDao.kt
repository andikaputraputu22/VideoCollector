package com.anankastudio.videocollector.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.anankastudio.videocollector.models.room.FavoriteVideo

@Dao
interface FavoriteVideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(favoriteVideo: FavoriteVideo)
}