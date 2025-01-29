package com.anankastudio.videocollector.viewholders

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.databinding.ItemVideoBinding
import com.anankastudio.videocollector.interfaces.OnClickVideo
import com.anankastudio.videocollector.models.item.ContentVideo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target

class ItemContentVideo(
    private val binding: ItemVideoBinding,
    private val onClickVideo: OnClickVideo
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ContentVideo) {
        val data = item.item
        binding.coverVideo.post {
            val imageWidth = data?.width ?: 0
            val imageHeight = data?.height ?: 0
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
            .load(data?.image)
            .override(Target.SIZE_ORIGINAL)
            .placeholder(ColorDrawable(Color.GRAY))
            .into(binding.coverVideo)

        itemView.setOnClickListener {
            onClickVideo.onClickDetail(data?.id ?: 0L)
        }
    }
}