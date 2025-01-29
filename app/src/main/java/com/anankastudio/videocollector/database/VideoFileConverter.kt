package com.anankastudio.videocollector.database

import androidx.room.TypeConverter
import com.anankastudio.videocollector.models.VideoFile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class VideoFileConverter {

    @TypeConverter
    fun fromVideoFileList(value: List<VideoFile>): String {
        val gson = Gson()
        val type = object : TypeToken<List<VideoFile>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toVideoFileList(value: String): List<VideoFile> {
        val gson = Gson()
        val type = object : TypeToken<List<VideoFile>>() {}.type
        return gson.fromJson(value, type)
    }
}