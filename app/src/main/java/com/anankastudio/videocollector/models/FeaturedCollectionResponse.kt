package com.anankastudio.videocollector.models

import com.google.gson.annotations.SerializedName

data class FeaturedCollectionResponse(
    val page: Int? = null,
    @SerializedName("per_page")
    val perPage: Int? = null,
    val collections: List<Collection>? = null,
    @SerializedName("total_results")
    val totalResults: Int? = null,
    @SerializedName("next_page")
    val nextPage: String? = null,
    @SerializedName("prev_page")
    val prevPage: String? = null
)
