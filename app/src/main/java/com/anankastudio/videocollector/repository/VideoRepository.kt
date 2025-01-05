package com.anankastudio.videocollector.repository

import com.anankastudio.videocollector.api.ApiService
import com.anankastudio.videocollector.database.DetailVideoDao
import com.anankastudio.videocollector.models.FeaturedCollectionResponse
import com.anankastudio.videocollector.models.PopularResponse
import com.anankastudio.videocollector.models.Video
import com.anankastudio.videocollector.models.item.ContentCollection
import com.anankastudio.videocollector.models.item.DataContentCollection
import com.anankastudio.videocollector.models.room.DetailVideo
import com.anankastudio.videocollector.utilities.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val apiService: ApiService,
    private val detailVideoDao: DetailVideoDao
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
        query: String = "",
        orientation: String = "",
        size: String = ""
    ): Result<PopularResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getSearchVideo(page, query, orientation, size)
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

    private suspend fun fetchFeaturedCollection(page: Int): FeaturedCollectionResponse? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFeaturedCollection(page)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchAllCollectionItems(page: Int): Result<DataContentCollection> = withContext(Dispatchers.IO) {
        try {
            val featuredCollection = fetchFeaturedCollection(page)
                ?: return@withContext Result.Error("Empty collection")
            val collections = featuredCollection.collections?.filter {
                it.videosCount != null && it.videosCount != 0
            } ?: emptyList()

            val deferredResponses = collections.map { collection ->
                async {
                    try {
                        val response = apiService.getContentCollection(
                            collection.id ?: "",
                            1,
                            "videos",
                            "desc"
                        )

                        if (response.isSuccessful) {
                            val items = response.body()?.media?.take(4) ?: emptyList()
                            ContentCollection(
                                id = collection.id,
                                title = collection.title,
                                count = collection.videosCount,
                                items = items
                            )
                        } else {
                            ContentCollection(
                                id = collection.id,
                                title = collection.title,
                                count = collection.videosCount,
                                items = emptyList()
                            )
                        }
                    } catch (e: Exception) {
                        ContentCollection(
                            id = collection.id,
                            title = collection.title,
                            count = collection.videosCount,
                            items = emptyList()
                        )
                    }
                }
            }

            val results = deferredResponses.awaitAll()
            val finalData = DataContentCollection(
                perPage = featuredCollection.perPage,
                totalResults = featuredCollection.totalResults,
                items = results
            )
            Result.Success(finalData)
        } catch (e: Exception) {
            Result.Error("Empty collection")
        }
    }

    suspend fun fetchDetailVideo(id: Long): Result<DetailVideo?> = withContext(Dispatchers.IO) {
        try {
            val cachedVideo = detailVideoDao.getVideoById(id)
            if (cachedVideo != null) {
                val currentTime = System.currentTimeMillis()
                val timeDifference = currentTime - cachedVideo.timestamp
                val interval = 600000

                if (timeDifference < interval) {
                    return@withContext Result.Success(cachedVideo)
                }
            }

            val response = apiService.getDetailVideo(id.toString())
            if (response.isSuccessful) {
                response.body()?.let {
                    saveVideoToDatabase(it)
                    val updatedVideo = detailVideoDao.getVideoById(id)
                    return@withContext Result.Success(updatedVideo)
                }
                cachedVideo?.let {
                    Result.Success(it)
                } ?: Result.Error("Response body is null and no cache data available")
            } else {
                cachedVideo?.let {
                    Result.Success(it)
                } ?: Result.Error("Failed to fetch data: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Exception occurred: ${e.message}")
        }
    }

    private suspend fun saveVideoToDatabase(data: Video) {
        data.id?.let {
            detailVideoDao.deleteVideo(it)
            val video = DetailVideo(
                id = it,
                width = data.width,
                height = data.height,
                duration = data.duration,
                url = data.url,
                image = data.image,
                userName = data.user?.name,
                userUrl = data.user?.url,
                videoFiles = data.videoFiles,
                timestamp = System.currentTimeMillis()
            )
            detailVideoDao.insertVideo(video)
        }
    }
}