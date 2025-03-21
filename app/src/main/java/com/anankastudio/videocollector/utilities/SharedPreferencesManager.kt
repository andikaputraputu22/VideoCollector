package com.anankastudio.videocollector.utilities

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val PREFERENCES_NAME = "video_collector_pref"
        const val FILTER_ORIENTATION = "filter_orientation"
        const val FILTER_SIZE = "filter_size"
        const val IS_FIRST_LAUNCH = "is_first_launch"

        const val WIDGET_LIST_KEYWORD = "widget_list_keyword"
        const val WIDGET_ORIENTATION = "widget_orientation"
        const val WIDGET_REFRESH_TIME = "widget_refresh_time"
        const val WIDGET_TOTAL_VIDEO = "widget_total_video"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun setString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: ""
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun setBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long = 0): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    fun setLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun setInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }
}