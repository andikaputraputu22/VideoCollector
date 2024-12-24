package com.anankastudio.videocollector.models

import com.google.gson.annotations.SerializedName

data class PopularResponse(
    val page: Int? = null,
    @SerializedName("per_page")
    val perPage: Int? = null,
    val videos: List<Video>? = null,
    @SerializedName("total_results")
    val totalResults: Int? = null,
    @SerializedName("next_page")
    val nextPage: String? = null,
    val url: String? = null
)
