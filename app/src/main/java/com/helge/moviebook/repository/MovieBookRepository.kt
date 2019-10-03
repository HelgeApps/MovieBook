package com.helge.moviebook.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.helge.moviebook.repository.db.RecentEntity
import com.helge.moviebook.repository.network.RemoteDataSource
import com.helge.moviebook.vo.Movie
import com.helge.moviebook.vo.RecentQuery

class MovieBookRepository(
    private val store: RecentEntity.Store,
    private val remoteDataSource: RemoteDataSource
) {

    /**
     * Data source search request
     */
    fun findMovie(query: String): Listing<Movie> {
        return remoteDataSource.findMovie(query)
    }

    /**
     * Data source movie details request
     */
    fun getMovieDetails(imdbID: String): MovieDetailsRequest {
        return remoteDataSource.getMovieDetails(imdbID)
    }

    /**
     * Get recent queries from local database
     */
    fun recentQueries(): LiveData<List<RecentQuery>> =
            Transformations.map(store.all()) { all -> all.map { it.toModel() } }

    suspend fun saveMovieQuery(recentQuery: RecentQuery) {
        store.save(RecentEntity(recentQuery))
        store.limitRows()
    }

    suspend fun deleteMovieQuery(recentQuery: RecentQuery) {
        store.delete(RecentEntity(recentQuery))
    }
}
