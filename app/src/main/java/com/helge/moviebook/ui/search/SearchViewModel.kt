package com.helge.moviebook.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helge.moviebook.repository.MovieBookRepository
import com.helge.moviebook.vo.RecentQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: MovieBookRepository) : ViewModel() {
    private val movieQuery = MutableLiveData<String>()
    private val repoResult = Transformations.map(movieQuery) {
        repository.findMovie(it)
    }
    val movies = Transformations.switchMap(repoResult) { it.pagedList }
    val networkState = Transformations.switchMap(repoResult) { it.networkState }
    val totalResults = Transformations.switchMap(repoResult) { it.totalResults }

    fun findMovie(query: String) {
        if (movieQuery.value == query) {
            return
        }
        movieQuery.value = query

        saveMovieQuery(query)
    }

    /**
     * retry movie search
     */
    fun retry() {
        repoResult.value?.retry?.invoke()
    }

    /**
     * cancel movie search
     */
    fun cancel() {
        repoResult.value?.cancel?.invoke()
    }

    /**
     * Save a [query] to Database Recent Queries Table
     */
    private fun saveMovieQuery(query: String) {
        val recentQuery = RecentQuery(query)
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveMovieQuery(recentQuery)
        }
    }
}