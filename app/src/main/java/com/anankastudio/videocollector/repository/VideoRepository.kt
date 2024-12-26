package com.anankastudio.videocollector.repository

import com.anankastudio.videocollector.api.ApiService
import com.anankastudio.videocollector.models.PopularResponse
import com.anankastudio.videocollector.utilities.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun fetchPopularVideo(page: Int): Result<PopularResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPopularVideo(page)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Response body is null")
            } else {
                Result.Error("Failed to fetch data: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Exception occurred: ${e.message}")
        }
    }

    suspend fun fetchSearchVideo(
        page: Int,
        query: String = ""
    ): Result<PopularResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getSearchVideo(page, query)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Response body is null")
            } else {
                Result.Error("Failed to fetch data: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Exception occurred: ${e.message}")
        }
    }
}