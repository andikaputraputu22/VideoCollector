package com.anankastudio.videocollector.api

import com.anankastudio.videocollector.models.CollectionResponse
import com.anankastudio.videocollector.models.FeaturedCollectionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("collections/featured")
    suspend fun getFeaturedCollection(
        @Query("page") page: Int
    ): Response<FeaturedCollectionResponse>

    @GET("collections/{id}")
    suspend fun getContentCollection(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("type") type: String,
        @Query("sort") sort: String
    ): Response<CollectionResponse>
}