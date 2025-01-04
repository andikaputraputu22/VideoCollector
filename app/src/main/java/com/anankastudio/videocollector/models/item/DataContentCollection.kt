package com.anankastudio.videocollector.models.item

import com.anankastudio.videocollector.interfaces.ExplorePage

data class DataContentCollection(
    var perPage: Int? = null,
    var totalResults: Int? = null,
    var items: List<ContentCollection>? = null
) : ExplorePage
