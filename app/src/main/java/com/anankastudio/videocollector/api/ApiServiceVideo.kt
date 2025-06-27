package com.anankastudio.videocollector.api

import com.anankastudio.videocollector.models.PopularResponse
import com.anankastudio.videocollector.models.Video
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceVideo {

    @GET("popular")
    suspend fun getPopularVideo(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 10,
    ): Response<PopularResponse>

    @GET("search")
    suspend fun getSearchVideo(
        @Query("page") page: Int,
        @Query("query") query: String,
        @Query("orientation") orientation: String,
        @Query("size") size: String
    ): Response<PopularResponse>

    @GET("videos/{id}")
    suspend fun getDetailVideo(
        @Path("id") id: String
    ): Response<Video>

    @GET("search")
    suspend fun getWidgetVideo(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("query") query: String,
        @Query("orientation") orientation: String
    ): Response<PopularResponse>
}