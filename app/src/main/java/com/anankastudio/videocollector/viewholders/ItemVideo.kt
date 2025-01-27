package com.anankastudio.videocollector.viewholders

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.databinding.ItemVideoBinding
import com.anankastudio.videocollector.interfaces.OnClickVideo
import com.anankastudio.videocollector.interfaces.VideoList
import com.anankastudio.videocollector.models.Video
import com.anankastudio.videocollector.models.room.FavoriteVideo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target

class ItemVideo(
    private val binding: ItemVideoBinding,
    private val onClickVideo: OnClickVideo
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: VideoList) {
        if (item is Video) {
            binding.coverVideo.post {
                val imageWidth = item.width ?: 0
                val imageHeight = item.height ?: 0
                val aspectRatio = if (imageHeight != 0) {
                    imageWidth.toFloat() / imageHeight
                } else {
                    1f
                }
                val newHeight = (binding.coverVideo.width / aspectRatio).toInt()
                binding.coverVideo.layoutParams = binding.coverVideo.layoutParams.apply {
                    height = newHeight
                }
            }

            Glide.with(itemView.context)
                .load(item.image)
                .override(Target.SIZE_ORIGINAL)
                .placeholder(ColorDrawable(Color.GRAY))
                .into(binding.coverVideo)

            itemView.setOnClickListener {
                onClickVideo.onClickDetail(item.id ?: 0L)
            }
        }

        if (item is FavoriteVideo) {
            binding.coverVideo.post {
                val imageWidth = item.width ?: 0
                val imageHeight = item.height ?: 0
                val aspectRatio = if (imageHeight != 0) {
                    imageWidth.toFloat() / imageHeight
                } else {
                    1f
                }
                val newHeight = (binding.coverVideo.width / aspectRatio).toInt()
                binding.coverVideo.layoutParams = binding.coverVideo.layoutParams.apply {
                    height = newHeight
                }
            }

            Glide.with(itemView.context)
                .load(item.image)
                .override(Target.SIZE_ORIGINAL)
                .placeholder(ColorDrawable(Color.GRAY))
                .into(binding.coverVideo)

            itemView.setOnClickListener {
                onClickVideo.onClickDetail(item.id)
            }
        }
    }
}