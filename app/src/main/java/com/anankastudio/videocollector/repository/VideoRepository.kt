package com.anankastudio.videocollector.repository

import com.anankastudio.videocollector.api.ApiService
import com.anankastudio.videocollector.models.Collection
import com.anankastudio.videocollector.models.FeaturedCollectionResponse
import com.anankastudio.videocollector.models.PopularResponse
import com.anankastudio.videocollector.models.item.ContentCollection
import com.anankastudio.videocollector.models.item.DataContentCollection
import com.anankastudio.videocollector.utilities.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
}