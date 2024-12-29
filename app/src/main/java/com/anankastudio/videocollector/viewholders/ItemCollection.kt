package com.anankastudio.videocollector.viewholders

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.databinding.ItemCollectionBinding
import com.anankastudio.videocollector.models.Video
import com.bumptech.glide.Glide

class ItemCollection(
    private val binding: ItemCollectionBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Video, position: Int) {
        Glide.with(itemView.context)
            .load(item.image)
            .placeholder(ColorDrawable(Color.GRAY))
            .into(binding.coverVideo)

        when(position) {
            0 -> binding.cardCoverVideo.setBackgroundResource(R.drawable.top_left_rounded)
            1 -> binding.cardCoverVideo.setBackgroundResource(R.drawable.top_right_rounded)
            2 -> binding.cardCoverVideo.setBackgroundResource(R.drawable.bottom_left_rounded)
            else -> binding.cardCoverVideo.setBackgroundResource(R.drawable.bottom_right_rounded)
        }
    }
}