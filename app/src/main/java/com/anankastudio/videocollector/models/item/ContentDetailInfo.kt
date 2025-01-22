package com.anankastudio.videocollector.models.item

import com.anankastudio.videocollector.interfaces.DetailPage
import com.anankastudio.videocollector.models.VideoFile

data class ContentDetailInfo(
    var fileSize: String? = "",
    var fps: String? = "",
    var duration: String? = "",
    var videoFile: VideoFile? = null
) : DetailPage
