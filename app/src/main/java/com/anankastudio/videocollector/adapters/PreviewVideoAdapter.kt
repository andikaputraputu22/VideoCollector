package com.anankastudio.videocollector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.databinding.ItemDetailPreviewVideoBinding
import com.anankastudio.videocollector.models.VideoPicture
import com.anankastudio.videocollector.viewholders.ItemDetailPreviewVideo

class PreviewVideoAdapter : RecyclerView.Adapter<ItemDetailPreviewVideo>() {

    private var listPreviewVideo: MutableList<VideoPicture> = mutableListOf()
    private var width: Int = 16
    private var height: Int = 9

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDetailPreviewVideo {
        val binding = ItemDetailPreviewVideoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemDetailPreviewVideo(binding, width, height)
    }

    override fun getItemCount() = listPreviewVideo.size

    override fun onBindViewHolder(holder: ItemDetailPreviewVideo, position: Int) {
        holder.bind(listPreviewVideo[position])
    }

    fun setData(items: List<VideoPicture>, width: Int, height: Int) {
        listPreviewVideo.clear()
        listPreviewVideo.addAll(items)
        this.width = width
        this.height = height
        notifyDataSetChanged()
    }
}