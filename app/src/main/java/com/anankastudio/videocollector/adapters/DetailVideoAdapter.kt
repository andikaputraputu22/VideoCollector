package com.anankastudio.videocollector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.databinding.ItemDetailVideoBinding
import com.anankastudio.videocollector.interfaces.DetailPage
import com.anankastudio.videocollector.models.item.ContentDetailVideo
import com.anankastudio.videocollector.utilities.HolderDetailBind
import com.anankastudio.videocollector.utilities.VideoPlayerManager
import com.anankastudio.videocollector.viewholders.ItemDetailVideo

class DetailVideoAdapter(
    private val videoPlayerManager: VideoPlayerManager?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listDetail: MutableList<DetailPage> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            ITEM_VIDEO -> {
                val binding = ItemDetailVideoBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemDetailVideo(binding, videoPlayerManager)
            }
        }
        val binding = ItemDetailVideoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemDetailVideo(binding, videoPlayerManager)
    }

    override fun getItemCount() = listDetail.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? HolderDetailBind)?.bind(listDetail[position])
    }

    override fun getItemViewType(position: Int): Int {
        return when(listDetail[position]) {
            is ContentDetailVideo -> return ITEM_VIDEO
            else -> ITEM_VIDEO
        }
    }

    fun setData(items: MutableList<DetailPage>) {
        listDetail = items
        notifyDataSetChanged()
    }

    companion object {
        const val ITEM_VIDEO = 1
    }
}