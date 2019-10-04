package com.helge.moviebook.ui.search

import com.helge.moviebook.api.OMDbApi
import com.helge.moviebook.vo.Movie
import com.helge.moviebook.vo.MovieDetails
import com.helge.moviebook.vo.MovieSearch
import retrofit2.Call
import retrofit2.mock.Calls

/**
 * Fake backend responses
 */
class FakeOMDbApi : OMDbApi {
    override fun getMovies(query: String, page: Int): Call<MovieSearch> {
        val movieSearch = if (page == 1) {
            val movieList = listOf(
                Movie(
                    "Thor",
                    "2011",
                    "tt0800369",
                    "movie",
                    "N/A"
                )
            )
            MovieSearch(movieList, "1", "True")
        } else {
            MovieSearch(null, "0", "False")
        }
        return Calls.response(movieSearch)
    }

    override fun getMovieDetails(imdbID: String): Call<MovieDetails> {
        return Calls.response(MovieDetails())
    }
}