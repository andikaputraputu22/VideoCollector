package com.anankastudio.videocollector.viewholders

import com.anankastudio.videocollector.databinding.ItemDetailInfoBinding
import com.anankastudio.videocollector.models.item.ContentDetailInfo
import com.anankastudio.videocollector.utilities.HolderDetail

class ItemDetailInfo(
    override val binding: ItemDetailInfoBinding
) : HolderDetail<ContentDetailInfo>(binding) {

    override fun onBind() {
        val resolution = "${data.videoFile?.width}x${data.videoFile?.height}"
        with(binding) {
            videoType.text = data.videoFile?.fileType
            videoResolution.text = resolution
            videoQuality.text = data.videoFile?.quality?.uppercase()
            videoSize.text = data.fileSize
            videoFps.text = data.fps
            videoDuration.text = data.duration
        }
    }
}