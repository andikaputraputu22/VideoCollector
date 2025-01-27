package com.anankastudio.videocollector.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anankastudio.videocollector.interfaces.VideoList

@Entity(tableName = "favorite_video")
data class FavoriteVideo(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val width: Int? = null,
    val height: Int? = null,
    val image: String? = null,
    val timestamp: Long
) : VideoList
