package com.anankastudio.videocollector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.databinding.ItemDownloadSizeBinding
import com.anankastudio.videocollector.interfaces.OnSelectDownloadSize
import com.anankastudio.videocollector.models.VideoFile

class DownloadSizeAdapter(
    private val listVideo: List<VideoFile>,
    private val listener: OnSelectDownloadSize
) : RecyclerView.Adapter<DownloadSizeAdapter.ViewHolder>() {

    private var itemLayout: MutableList<LinearLayout> = mutableListOf()

    class ViewHolder(val binding: ItemDownloadSizeBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemDownloadSizeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount() = listVideo.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listVideo[position]
        itemLayout.add(holder.binding.containerLayout)
        setBackground(holder.binding.containerLayout, position == 0)

        val quality = if ((data.width == 1920 || data.height == 1080) ||
            (data.width == 1080 || data.height == 1920)) {
            "Full HD"
        } else if (data.quality == "uhd") {
            "Ultra HD"
        } else {
            data.quality?.uppercase()
        }
        val resolution = "${data.width} x ${data.height} pixels"
        holder.binding.quality.text = quality
        holder.binding.resolution.text = resolution

        if (position == 0) {
            data.link?.let { listener.onClickSize(it) }
        }

        holder.itemView.setOnClickListener {
            data.link?.let { url -> listener.onClickSize(url) }
            setTempBackground(holder.binding.containerLayout)
        }
    }

    private fun setBackground(layout: LinearLayout, isSelected: Boolean) {
        layout.setBackgroundResource(
            if (isSelected) R.drawable.bg_filter_selected
            else R.drawable.bg_filter
        )
    }

    private fun setTempBackground(layout: LinearLayout) {
        itemLayout.forEach {
            it.setBackgroundResource(R.drawable.bg_filter)
        }

        layout.setBackgroundResource(R.drawable.bg_filter_selected)
    }
}