package com.helge.moviebook.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.helge.moviebook.repository.network.NetworkState

/**
 * Data class that is necessary for a UI to show a listing and interact w/ the rest of the system
 */
data class Listing<T>(
    // the LiveData of paged lists for the UI to observe
    val pagedList: LiveData<PagedList<T>>,
    // the LiveData of total items count for the UI to observe
    val totalResults: LiveData<Int?>,
    // represents the network request status to show to the user
    val networkState: LiveData<NetworkState>,
    // retries any failed requests
    val retry: () -> Unit,
    // cancel the current request
    val cancel: () -> Unit
)