package com.anankastudio.videocollector.api

import com.anankastudio.videocollector.models.PopularResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("popular")
    suspend fun getPopularVideo(
        @Query("page") page: Int
    ): Response<PopularResponse>

    @GET("search")
    suspend fun getSearchVideo(
        @Query("page") page: Int,
        @Query("query") query: String
    ): Response<PopularResponse>
}