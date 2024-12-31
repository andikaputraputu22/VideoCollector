package com.anankastudio.videocollector.models

import com.google.gson.annotations.SerializedName

data class Video(
    val type: String? = null,
    val id: Long? = null,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Int? = null,
    val url: String? = null,
    val image: String? = null,
    val user: User? = null,
    @SerializedName("video_files")
    val videoFiles: List<VideoFile>,
    @SerializedName("video_pictures")
    val videoPictures: List<VideoPicture>
)
