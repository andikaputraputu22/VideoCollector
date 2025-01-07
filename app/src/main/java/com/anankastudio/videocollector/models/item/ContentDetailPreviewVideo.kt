package com.anankastudio.videocollector.models.item

import com.anankastudio.videocollector.interfaces.DetailPage
import com.anankastudio.videocollector.models.VideoPicture

data class ContentDetailPreviewVideo(
    var items: List<VideoPicture>? = null
) : DetailPage
