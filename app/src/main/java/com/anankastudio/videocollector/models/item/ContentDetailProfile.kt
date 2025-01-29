package com.anankastudio.videocollector.models.item

import com.anankastudio.videocollector.interfaces.DetailPage

data class ContentDetailProfile(
    var userName: String? = null,
    var userUrl: String? = null
) : DetailPage
