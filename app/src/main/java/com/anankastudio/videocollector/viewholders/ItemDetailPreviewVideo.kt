package com.anankastudio.videocollector.viewholders

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.databinding.ItemDetailPreviewVideoBinding
import com.anankastudio.videocollector.models.VideoPicture
import com.bumptech.glide.Glide

class ItemDetailPreviewVideo(
    private val binding: ItemDetailPreviewVideoBinding,
    private val width: Int,
    private val height: Int
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: VideoPicture) {
        val ratio = "$width:$height"
        binding.imageContainer.layoutParams =
            (binding.imageContainer.layoutParams as ConstraintLayout.LayoutParams)
                .apply {
                    dimensionRatio = ratio
                }

        Glide.with(itemView.context)
            .load(item.picture)
            .placeholder(ColorDrawable(Color.GRAY))
            .into(binding.previewImage)
    }
}