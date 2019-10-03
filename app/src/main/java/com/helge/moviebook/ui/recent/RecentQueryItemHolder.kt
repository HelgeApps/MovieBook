package com.helge.moviebook.ui.recent

import androidx.recyclerview.widget.RecyclerView
import com.helge.moviebook.databinding.RecentQueryItemBinding
import com.helge.moviebook.vo.RecentQuery

class RecentQueryItemHolder(
    private val binding: RecentQueryItemBinding,
    val onRowClick: (RecentQuery) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: RecentQuery) {
        binding.model = model
        binding.holder = this
        binding.executePendingBindings()
    }
}