package com.anankastudio.videocollector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.databinding.ItemSearchHistoryBinding
import com.anankastudio.videocollector.interfaces.OnSelectSearchHistory
import com.anankastudio.videocollector.models.room.SearchHistory

class SearchHistoryAdapter(
    private var listSearchHistory: List<SearchHistory>,
    private val listener: OnSelectSearchHistory
) : RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSearchHistoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemSearchHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount() = listSearchHistory.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listSearchHistory[position]
        holder.binding.keyword.text = data.keyword
        holder.itemView.setOnClickListener {
            data.keyword?.let { keyword -> listener.onClickKeyword(keyword) }
        }
    }

    fun updateList(items: List<SearchHistory>) {
        listSearchHistory = items
        notifyDataSetChanged()
    }
}