package com.anankastudio.videocollector.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val keyword: String? = "",
    val timestamp: Long
)
