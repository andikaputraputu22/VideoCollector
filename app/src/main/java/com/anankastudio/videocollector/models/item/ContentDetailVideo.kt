package com.anankastudio.videocollector.models.item

import com.anankastudio.videocollector.interfaces.DetailPage
import com.anankastudio.videocollector.models.VideoFile

data class ContentDetailVideo(
    var item: VideoFile? = null
) : DetailPage
