package com.anankastudio.videocollector.database

import androidx.room.TypeConverter
import com.anankastudio.videocollector.models.VideoPicture
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class VideoPictureConverter {

    @TypeConverter
    fun fromVideoFileList(value: List<VideoPicture>): String {
        val gson = Gson()
        val type = object : TypeToken<List<VideoPicture>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toVideoFileList(value: String): List<VideoPicture> {
        val gson = Gson()
        val type = object : TypeToken<List<VideoPicture>>() {}.type
        return gson.fromJson(value, type)
    }
}