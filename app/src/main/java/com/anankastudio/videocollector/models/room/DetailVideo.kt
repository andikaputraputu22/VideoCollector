package com.anankastudio.videocollector.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anankastudio.videocollector.models.VideoFile

@Entity(tableName = "detail_video")
data class DetailVideo(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Int? = null,
    val url: String? = null,
    val image: String? = null,
    val userName: String? = null,
    val userUrl: String? = null,
    val videoFiles: List<VideoFile>? = null,
    val timestamp: Long
)
