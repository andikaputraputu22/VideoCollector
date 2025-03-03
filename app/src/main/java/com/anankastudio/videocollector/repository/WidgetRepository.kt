package com.anankastudio.videocollector.repository

import android.content.Context
import com.anankastudio.videocollector.api.ApiService
import com.anankastudio.videocollector.database.WidgetVideoDao
import com.anankastudio.videocollector.models.PopularResponse
import com.anankastudio.videocollector.models.room.WidgetVideo
import com.anankastudio.videocollector.utilities.Result
import com.anankastudio.videocollector.widget.SlideWidgetHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WidgetRepository @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val widgetVideoDao: WidgetVideoDao
) {

    suspend fun fetchWidgetVideo(
        query: String = ""
    ): Result<PopularResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getWidgetVideo(1, 7, query)
            if (response.isSuccessful) {
                response.body()?.let {
                    saveVideoToDatabase(it)
                    return@withContext Result.Success(it)
                } ?: return@withContext Result.Error("Response body is null")
            } else {
                return@withContext Result.Error("Failed to fetch data: ${response.code()}")
            }
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

        widgetVideoDao.deleteAllWidgetVideo()
        widgetVideoDao.insertAllWidgetVideo(widgetVideos)
        SlideWidgetHelper.sendWidgetUpdateBroadcast(context)
    }
}