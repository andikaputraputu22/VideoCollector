package com.anankastudio.videocollector.viewholders

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.adapters.PreviewVideoAdapter
import com.anankastudio.videocollector.databinding.ItemDetailPreviewVideoContainerBinding
import com.anankastudio.videocollector.models.item.ContentDetailPreviewVideo
import com.anankastudio.videocollector.utilities.HolderDetail
import com.anankastudio.videocollector.utilities.SpaceItemDecoration

class ItemDetailPreviewVideoContainer(
    override val binding: ItemDetailPreviewVideoContainerBinding
) : HolderDetail<ContentDetailPreviewVideo>(binding) {

    private val adapter = PreviewVideoAdapter()

    override fun onBind() {
        if (data.items?.isEmpty() == true) {
            itemView.visibility = View.GONE
            itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        } else {
            itemView.visibility = View.VISIBLE
            itemView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            binding.rvPreviewVideo.layoutManager = LinearLayoutManager(
                itemView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.rvPreviewVideo.adapter = adapter
            val space = itemView.resources.getDimensionPixelSize(R.dimen.item_spacing_video)
            clearItemDecorations(binding.rvPreviewVideo)
            binding.rvPreviewVideo.addItemDecoration(SpaceItemDecoration(space))
            data.items?.let { adapter.setData(it, data.width ?: 16, data.height ?: 9) }
        }
    }

    private fun clearItemDecorations(recyclerView: RecyclerView) {
        while (recyclerView.itemDecorationCount > 0) {
            recyclerView.removeItemDecorationAt(0)
        }
    }
}