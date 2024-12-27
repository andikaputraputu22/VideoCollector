package com.anankastudio.videocollector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.databinding.ItemVideoBinding
import com.anankastudio.videocollector.interfaces.ExplorePage
import com.anankastudio.videocollector.models.item.ContentVideo
import com.anankastudio.videocollector.viewholders.ItemContentVideo

class ExploreAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listVideo: MutableList<ExplorePage> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            CONTENT_VIDEO -> {
                val binding = ItemVideoBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemContentVideo(binding)
            }
        }
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemContentVideo(binding)
    }

    override fun getItemCount() = listVideo.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ItemContentVideo -> holder.bind(listVideo[position] as ContentVideo)
        }
    }

    override fun getItemViewType(position: Int): Int {
        when(listVideo[position]) {
            is ContentVideo -> return CONTENT_VIDEO
        }
        return CONTENT_VIDEO
    }

    fun setData(items: List<ExplorePage>) {
        listVideo.addAll(items)
        notifyItemRangeInserted((listVideo.size - items.size), items.size)
    }

    fun clear() {
        listVideo.clear()
        notifyDataSetChanged()
    }

    companion object {
        const val CONTENT_VIDEO = 1
    }
}