package com.anankastudio.videocollector.models.item

import com.anankastudio.videocollector.interfaces.ExplorePage
import com.anankastudio.videocollector.models.Video

data class ContentVideo(
    var item: Video? = null
) : ExplorePage
