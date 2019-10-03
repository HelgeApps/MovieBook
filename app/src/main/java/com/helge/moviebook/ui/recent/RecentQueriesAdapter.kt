package com.helge.moviebook.ui.recent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.helge.moviebook.databinding.RecentQueryItemBinding
import com.helge.moviebook.vo.RecentQuery

class RecentQueriesAdapter(
    private val inflater: LayoutInflater,
    private val onRowClick: (RecentQuery) -> Unit
) : ListAdapter<RecentQuery, RecentQueryItemHolder>(DiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) =
        RecentQueryItemHolder(
            RecentQueryItemBinding.inflate(
                inflater,
                parent,
                false
            ),
            onRowClick
        )

    override fun onBindViewHolder(holder: RecentQueryItemHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private object DiffCallback : DiffUtil.ItemCallback<RecentQuery>() {
    override fun areItemsTheSame(oldItem: RecentQuery, newItem: RecentQuery) =
        oldItem.query == newItem.query

    override fun areContentsTheSame(oldItem: RecentQuery, newItem: RecentQuery) =
        oldItem == newItem
}