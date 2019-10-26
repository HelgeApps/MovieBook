package com.helge.moviebook.ui

import androidx.lifecycle.ViewModel

/**
 * is used for communicating between fragments
 * (passing query from RecentFragment to SearchFragment)
 */
class SharedViewModel : ViewModel() {
    var movieSearchQuery: String? = null
}