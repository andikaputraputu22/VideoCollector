package com.anankastudio.videocollector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.databinding.ItemVideoBinding
import com.anankastudio.videocollector.interfaces.ExplorePage
import com.anankastudio.videocollector.models.item.LandscapeVideo
import com.anankastudio.videocollector.models.item.PortraitVideo
import com.anankastudio.videocollector.models.item.RandomVideo
import com.anankastudio.videocollector.viewholders.ItemRandomVideo

class ExploreAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listVideo: MutableList<ExplorePage> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            RANDOM_VIDEO -> {
                val binding = ItemVideoBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemRandomVideo(binding)
            }
        }
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemRandomVideo(binding)
    }

    override fun getItemCount() = listVideo.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ItemRandomVideo -> holder.bind(listVideo[position] as RandomVideo)
        }
    }

    override fun getItemViewType(position: Int): Int {
        when(listVideo[position]) {
            is RandomVideo -> return RANDOM_VIDEO
            is PortraitVideo -> return PORTRAIT_VIDEO
            is LandscapeVideo -> return LANDSCAPE_VIDEO
        }
        return RANDOM_VIDEO
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
        const val RANDOM_VIDEO = 1
        const val PORTRAIT_VIDEO = 2
        const val LANDSCAPE_VIDEO = 3
    }
}