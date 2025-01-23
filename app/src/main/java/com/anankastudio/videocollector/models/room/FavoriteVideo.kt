package com.anankastudio.videocollector.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_video")
data class FavoriteVideo(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val width: Int? = null,
    val height: Int? = null,
    val image: String? = null
)
