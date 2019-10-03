package com.helge.moviebook.repository

import androidx.lifecycle.LiveData
import com.helge.moviebook.repository.network.NetworkState
import com.helge.moviebook.vo.MovieDetails

data class MovieDetailsRequest(
    // the LiveData of the movie details for the UI to observe
    val movieDetails: LiveData<MovieDetails>,
    // represents the network request status to show to the user
    val networkState: LiveData<NetworkState>,
    // retries any failed requests
    val retry: () -> Unit,
    // cancel the current request
    val cancel: () -> Unit
)