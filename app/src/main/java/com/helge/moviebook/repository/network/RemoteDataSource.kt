package com.helge.moviebook.repository.network

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.helge.moviebook.api.OMDbApi
import com.helge.moviebook.repository.Listing
import com.helge.moviebook.repository.MovieDetailsRequest
import com.helge.moviebook.vo.Movie
import com.helge.moviebook.vo.MovieDetails
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class RemoteDataSource(private val omDbApi: OMDbApi) {

    @Suppress("PrivatePropertyName")
    private val NETWORK_IO = Executors.newFixedThreadPool(5)

    @MainThread
    fun findMovie(query: String): Listing<Movie> {
        val sourceFactory = RemoteDataSourceFactory(omDbApi, query, NETWORK_IO)

        val livePagedList = sourceFactory.toLiveData(
            pageSize = 10,
            fetchExecutor = NETWORK_IO
        )

        return Listing(
            pagedList = livePagedList,
            totalResults = Transformations.switchMap(sourceFactory.sourceLiveData) {
                it.totalResults
            },
            networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
                it.networkState
            },
            retry = {
                sourceFactory.sourceLiveData.value?.retryAllFailed()
            },
            cancel = {
                sourceFactory.sourceLiveData.value?.cancelLoading()
            }
        )
    }

    @MainThread
    fun getMovieDetails(imdbID: String): MovieDetailsRequest {
        val movieDetails = MutableLiveData<MovieDetails>()
        val networkState = MutableLiveData<NetworkState>()

        networkState.postValue(NetworkState.LOADING)

        var request = omDbApi.getMovieDetails(imdbID)
        val callback = object : Callback<MovieDetails> {
            override fun onResponse(
                call: Call<MovieDetails>,
                response: Response<MovieDetails>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    movieDetails.postValue(response.body()!!)
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.ERROR)
                }
            }

            override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
                if (call.isCanceled) {
                    networkState.postValue(NetworkState.CANCELED)
                } else {
                    networkState.postValue(NetworkState.ERROR)
                }
            }
        }
        request.enqueue(callback)

        val retry = {
            networkState.postValue(NetworkState.LOADING)
            request = omDbApi.getMovieDetails(imdbID)
            request.enqueue(callback)
        }

        val cancel = {
            if (request.isExecuted && !request.isCanceled) {
                request.cancel()
            } else {
                networkState.postValue(NetworkState.CANCELED)
            }
        }

        return MovieDetailsRequest(movieDetails, networkState, retry, cancel)
    }
}
