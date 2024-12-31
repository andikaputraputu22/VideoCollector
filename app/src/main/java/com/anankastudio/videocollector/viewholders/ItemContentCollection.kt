package com.anankastudio.videocollector.viewholders

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.adapters.CollectionAdapter
import com.anankastudio.videocollector.databinding.ItemCollectionContainerBinding
import com.anankastudio.videocollector.models.item.ContentCollection
import com.anankastudio.videocollector.utilities.SpaceItemDecoration

class ItemContentCollection(
    private val binding: ItemCollectionContainerBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val adapter = CollectionAdapter()

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

            val count = "${item.count} ${itemView.context.getString(R.string.videos)}"
            binding.titleBox.text = item.title
            binding.count.text = count
            binding.rvCollection.layoutManager = GridLayoutManager(itemView.context, 2)
            binding.rvCollection.adapter = adapter
            val space = itemView.resources.getDimensionPixelSize(R.dimen.item_spacing_video)
            clearItemDecorations(binding.rvCollection)
            binding.rvCollection.addItemDecoration(SpaceItemDecoration(space))
            item.items?.let { adapter.setData(it) }
        }
    }

    private fun clearItemDecorations(recyclerView: RecyclerView) {
        while (recyclerView.itemDecorationCount > 0) {
            recyclerView.removeItemDecorationAt(0)
        }
    }
}