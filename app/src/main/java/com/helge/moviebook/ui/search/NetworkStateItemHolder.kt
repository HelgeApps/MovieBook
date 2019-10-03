package com.helge.moviebook.ui.search

import androidx.recyclerview.widget.RecyclerView
import com.helge.moviebook.databinding.NetworkStateItemBinding
import com.helge.moviebook.repository.network.NetworkState

class NetworkStateItemHolder(
    private val binding: NetworkStateItemBinding,
    val onRetryClick: () -> Unit,
    val onCancelClick: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(networkState: NetworkState?) {
        binding.networkState = networkState
        binding.holder = this
        binding.executePendingBindings()
    }
}