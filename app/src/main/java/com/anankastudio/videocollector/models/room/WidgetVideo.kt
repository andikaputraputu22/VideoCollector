package com.anankastudio.videocollector.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "widget_video")
data class WidgetVideo(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val image: String? = null,
    val title: String = "",
    val timestamp: Long
)
