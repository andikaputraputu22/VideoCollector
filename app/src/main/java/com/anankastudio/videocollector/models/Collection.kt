package com.anankastudio.videocollector.models

import com.google.gson.annotations.SerializedName

data class Collection(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val private: Boolean? = null,
    @SerializedName("media_count")
    val mediaCount: Int? = null,
    @SerializedName("photos_count")
    val photosCount: Int? = null,
    @SerializedName("videos_count")
    val videosCount: Int? = null
)
