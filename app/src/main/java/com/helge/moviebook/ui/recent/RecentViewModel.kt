package com.helge.moviebook.ui.recent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helge.moviebook.repository.MovieBookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecentViewModel(private val repository: MovieBookRepository) : ViewModel() {

    val recentQueries = repository.recentQueries()

    fun deleteMovieQuery(position: Int) {
        val model = recentQueries.value?.get(position) ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMovieQuery(model)
        }
    }
}