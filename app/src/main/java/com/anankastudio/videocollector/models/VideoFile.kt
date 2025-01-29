package com.anankastudio.videocollector.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
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
) : Parcelable
