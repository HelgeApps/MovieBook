package com.helge.moviebook.ui.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.helge.moviebook.repository.MovieBookRepository

class DetailsViewModel(private val repository: MovieBookRepository) : ViewModel() {
    private val imdbID = MutableLiveData<String>()
    private val repoResult = Transformations.map(imdbID) {
        repository.getMovieDetails(it)
    }
    val movieDetails = Transformations.switchMap(repoResult) { it.movieDetails }
    val networkState = Transformations.switchMap(repoResult) { it.networkState }

    fun getMovieDetails(imdbID: String) {
        this.imdbID.value = imdbID
    }

    fun retry() {
        repoResult.value?.retry?.invoke()
    }

    fun cancel() {
        repoResult.value?.cancel?.invoke()
    }
}