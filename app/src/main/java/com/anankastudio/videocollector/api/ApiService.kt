package com.anankastudio.videocollector.api

import com.anankastudio.videocollector.models.PopularResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("popular")
    suspend fun getPopularVideo(): Response<PopularResponse>
}