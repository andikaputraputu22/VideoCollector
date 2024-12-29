package com.anankastudio.videocollector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.databinding.ItemCollectionBinding
import com.anankastudio.videocollector.models.Video
import com.anankastudio.videocollector.viewholders.ItemCollection

class CollectionAdapter : RecyclerView.Adapter<ItemCollection>() {

    private var listVideo: MutableList<Video> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCollection {
        val binding = ItemCollectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemCollection(binding)
    }

    override fun getItemCount() = listVideo.size

    override fun onBindViewHolder(holder: ItemCollection, position: Int) {
        holder.bind(listVideo[position], position)
    }

    fun setData(items: List<Video>) {
        listVideo.clear()
        listVideo.addAll(items)
        notifyDataSetChanged()
    }
}