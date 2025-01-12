package com.anankastudio.videocollector.viewholders

import androidx.constraintlayout.widget.ConstraintLayout
import com.anankastudio.videocollector.databinding.ItemDetailVideoBinding
import com.anankastudio.videocollector.models.item.ContentDetailVideo
import com.anankastudio.videocollector.utilities.HolderDetail
import com.anankastudio.videocollector.utilities.VideoPlayerManager

class ItemDetailVideo(
    override val binding: ItemDetailVideoBinding,
    private val videoPlayerManager: VideoPlayerManager?
) : HolderDetail<ContentDetailVideo>(binding) {

    override fun onBind() {
        val width = data.item?.width ?: 16
        val height = data.item?.height ?: 9
        val ratio = "$width:$height"
        binding.playerView.layoutParams =
            (binding.playerView.layoutParams as ConstraintLayout.LayoutParams)
            .apply {
                dimensionRatio = ratio
            }

        videoPlayerManager?.release()
        videoPlayerManager?.setProgressBar(binding.progressBar)
        data.item?.link?.let {
            videoPlayerManager?.initializePlayer(
                it,
                videoPlayerManager.playerCallback
            ).also { player ->
                binding.playerView.player = player
            }
            videoPlayerManager?.setAutoPlay(true)
        }
    }
}