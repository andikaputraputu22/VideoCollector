package com.anankastudio.videocollector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anankastudio.videocollector.databinding.ItemKeywordWidgetBinding
import com.anankastudio.videocollector.interfaces.OnSelectKeywordWidget

class KeywordWidgetAdapter(
    private var listKeyword: MutableList<String>,
    private val listener: OnSelectKeywordWidget
) : RecyclerView.Adapter<KeywordWidgetAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemKeywordWidgetBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(keyword: String, position: Int) {
            binding.keyword.text = keyword
            binding.delete.setOnClickListener {
                listener.onDeleteKeyword(position, keyword)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKeywordWidgetBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = listKeyword.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listKeyword[position], position)
    }

    fun updateList(items: MutableList<String>) {
        listKeyword = items
        notifyDataSetChanged()
    }
}