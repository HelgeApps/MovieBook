package com.helge.moviebook.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.helge.GlideRequests
import com.helge.moviebook.R
import com.helge.moviebook.databinding.SearchMovieItemBinding
import com.helge.moviebook.databinding.NetworkStateBinding
import com.helge.moviebook.databinding.NetworkStateItemBinding
import com.helge.moviebook.repository.network.NetworkState
import com.helge.moviebook.vo.Movie

/**
 * Adapter implementation that shows movies returned from OMDbApi and also network state
 */
class SearchMoviesAdapter(
    private val inflater: LayoutInflater,
    private val glide: GlideRequests,
    private val onRetryClick: () -> Unit,
    private val onCancelClick: () -> Unit,
    private val onRowClick: (Movie) -> Unit
) : PagedListAdapter<Movie, RecyclerView.ViewHolder>(DiffCallback) {
    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.search_movie_item -> SearchMovieItemHolder(
                SearchMovieItemBinding.inflate(
                    inflater,
                    parent,
                    false
                ),
                glide,
                onRowClick
            )
            // display this not high row when there are some items in the list and network error
            R.layout.network_state_item -> {
                NetworkStateItemHolder(
                    NetworkStateItemBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onRetryClick,
                    onCancelClick
                )
            }
            // display this view which matches all height when there are no items in the list
            // and network error
            R.layout.network_state -> {
                NetworkStateHolder(
                    NetworkStateBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onRetryClick,
                    onCancelClick
                )
            }
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.search_movie_item -> (holder as SearchMovieItemHolder).bind(
                getItem(position)
            )
            R.layout.network_state_item -> (holder as NetworkStateItemHolder).bind(
                networkState
            )
            R.layout.network_state -> (holder as NetworkStateHolder).bind(
                networkState
            )
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            if (position == 0) {
                R.layout.network_state
            } else {
                R.layout.network_state_item
            }
        } else {
            R.layout.search_movie_item
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }
}

private object DiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areContentsTheSame(oldItem: Movie, newItem: Movie) =
        oldItem == newItem

    override fun areItemsTheSame(oldItem: Movie, newItem: Movie) =
        oldItem.imdbID == newItem.imdbID
}