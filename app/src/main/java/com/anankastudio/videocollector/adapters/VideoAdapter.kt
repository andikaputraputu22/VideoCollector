package com.anankastudio.videocollector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.databinding.ItemVideoBinding
import com.anankastudio.videocollector.models.Video
import com.anankastudio.videocollector.viewholders.ItemVideo

class VideoAdapter : RecyclerView.Adapter<ItemVideo>() {

    private var listVideo: MutableList<Video> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVideo {
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemVideo(binding)
    }

    override fun getItemCount() = listVideo.size

    override fun onBindViewHolder(holder: ItemVideo, position: Int) {
        holder.bind(listVideo[position])
    }

    fun setData(items: List<Video>) {
        listVideo.addAll(items)
        notifyItemRangeInserted((listVideo.size - items.size), items.size)
    }

    fun clear() {
        listVideo.clear()
        notifyDataSetChanged()
    }
}