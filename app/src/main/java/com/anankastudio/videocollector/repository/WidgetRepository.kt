package com.anankastudio.videocollector.repository

import android.content.Context
import com.anankastudio.videocollector.api.ApiService
import com.anankastudio.videocollector.database.WidgetVideoDao
import com.anankastudio.videocollector.models.PopularResponse
import com.anankastudio.videocollector.models.room.WidgetVideo
import com.anankastudio.videocollector.utilities.Result
import com.anankastudio.videocollector.utilities.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WidgetRepository @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val widgetVideoDao: WidgetVideoDao,
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    suspend fun fetchWidgetVideo(): Result<PopularResponse> = withContext(Dispatchers.IO) {
        try {
            val totalVideo = sharedPreferencesManager.getInt(SharedPreferencesManager.WIDGET_TOTAL_VIDEO, 15)
            val orientation = sharedPreferencesManager.getString(SharedPreferencesManager.WIDGET_ORIENTATION)
            val keywords = getKeywordWidget()
            var lastSuccess: PopularResponse? = null

            widgetVideoDao.deleteAllWidgetVideo()
            keywords.forEach { keyword ->
                val response = apiService.getWidgetVideo(1, totalVideo, keyword, orientation)
                if (response.isSuccessful) {
                    response.body()?.let {
                        saveVideoToDatabase(it)
                        lastSuccess = it
                    }
                }
            }

            return@withContext lastSuccess?.let { Result.Success(it) }
                ?: Result.Error("No successful results")
        } catch (e: Exception) {
            return@withContext Result.Error("Exception occurred: ${e.message}")
        }
    }

    private suspend fun saveVideoToDatabase(data: PopularResponse) {
        val widgetVideos = data.videos?.mapNotNull { video ->
            video.id?.let { id ->
                WidgetVideo(
                    id,
                    video.image,
                    video.user?.name ?: "",
                    System.currentTimeMillis()
                )
            }
        }.orEmpty()

        widgetVideoDao.insertAllWidgetVideo(widgetVideos)
    }

    private fun getKeywordWidget(): List<String> {
        val stringKeyword = sharedPreferencesManager.getString(SharedPreferencesManager.WIDGET_LIST_KEYWORD)
        val listKeyword = if (stringKeyword.isEmpty()) arrayListOf()
        else stringKeyword.split("#").toCollection(ArrayList())

        return listKeyword
    }
}