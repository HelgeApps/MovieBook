package com.helge.moviebook.ui.search

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.helge.GlideRequests
import com.helge.moviebook.R
import com.helge.moviebook.databinding.SearchMovieItemBinding
import com.helge.moviebook.vo.Movie

class SearchMovieItemHolder(
    private val binding: SearchMovieItemBinding,
    private val glide: GlideRequests,
    val onRowClick: (Movie) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Movie?) {
        movie ?: return
        binding.movie = movie
        binding.holder = this
        if (movie.poster != null && movie.poster != "N/A") {
            glide.load(movie.poster)
                .centerCrop()
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_movie_placeholder)
                .error(R.drawable.ic_movie_placeholder)
                .into(binding.thumb)
        } else {
            glide.load(R.drawable.ic_movie_placeholder)
                .into(binding.thumb)
        }
        binding.executePendingBindings()
    }
}