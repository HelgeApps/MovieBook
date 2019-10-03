package com.helge.moviebook.ui.search

import androidx.recyclerview.widget.RecyclerView
import com.helge.moviebook.databinding.NetworkStateBinding
import com.helge.moviebook.repository.network.NetworkState

class NetworkStateHolder(
    private val binding: NetworkStateBinding,
    private val onRetryClick: () -> Unit,
    private val onCancelClick: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(networkState: NetworkState?) {
        binding.networkState = networkState
        binding.cancel.setOnClickListener { onCancelClick.invoke() }
        binding.retry.setOnClickListener { onRetryClick.invoke() }
        binding.executePendingBindings()
    }
}