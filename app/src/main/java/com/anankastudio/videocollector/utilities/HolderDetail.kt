package com.anankastudio.videocollector.utilities

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.anankastudio.videocollector.interfaces.DetailPage

abstract class HolderDetail<D : DetailPage>(
    protected open val binding: ViewBinding
) : HolderDetailBind, RecyclerView.ViewHolder(binding.root) {

    protected lateinit var data: D
    protected abstract fun onBind()

    @Suppress("UNCHECKED_CAST")
    override fun bind(d: DetailPage) {
        with(d as D) {
            if (!::data.isInitialized || data != this) {
                data = this
                onBind()
            }
        }
    }
}

fun interface HolderDetailBind {
    fun bind(d: DetailPage)
}