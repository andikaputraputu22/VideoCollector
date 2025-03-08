package com.anankastudio.videocollector.api

import com.anankastudio.videocollector.models.CollectionResponse
import com.anankastudio.videocollector.models.FeaturedCollectionResponse
import com.anankastudio.videocollector.models.PopularResponse
import com.anankastudio.videocollector.models.Video
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("videos/popular")
    suspend fun getPopularVideo(
        @Query("page") page: Int
    ): Response<PopularResponse>

    @GET("videos/search")
    suspend fun getSearchVideo(
        @Query("page") page: Int,
        @Query("query") query: String,
        @Query("orientation") orientation: String,
        @Query("size") size: String
    ): Response<PopularResponse>

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

    @GET("videos/videos/{id}")
    suspend fun getDetailVideo(
        @Path("id") id: String
    ): Response<Video>

    @GET("videos/search")
    suspend fun getWidgetVideo(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("query") query: String
    ): Response<PopularResponse>
}