package com.anankastudio.videocollector.models

import com.google.gson.annotations.SerializedName

data class VideoFile(
    val id: Long? = null,
    val quality: String? = null,
    @SerializedName("file_type")
    val fileType: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val fps: Double? = null,
    val link: String? = null,
    val size: Long? = null
)
