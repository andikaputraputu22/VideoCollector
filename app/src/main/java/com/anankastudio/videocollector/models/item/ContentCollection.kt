package com.anankastudio.videocollector.models.item

import com.anankastudio.videocollector.interfaces.ExplorePage
import com.anankastudio.videocollector.models.Video

data class ContentCollection(
    var id: String? = null,
    var title: String? = null,
    var count: Int? = null,
    var items: List<Video>? = null
) : ExplorePage
