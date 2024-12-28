package com.anankastudio.videocollector.viewholders

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.databinding.ItemCollectionContainerBinding
import com.anankastudio.videocollector.models.item.ContentCollection

class ItemContentCollection(
    private val binding: ItemCollectionContainerBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ContentCollection) {
        if (item.items?.isEmpty() == true) {
            itemView.visibility = View.GONE
            itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        } else {
            itemView.visibility = View.VISIBLE
            itemView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            binding.titleBox.text = item.title
        }
    }
}