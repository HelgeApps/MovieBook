package com.helge.moviebook.repository.network

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.helge.moviebook.vo.MovieSearch
import com.helge.moviebook.api.OMDbApi
import com.helge.moviebook.vo.Movie
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

private const val FIRST_PAGE = 1

/**
 * A data source that uses the before/after keys returned in page requests.
 */
class RemotePageKeyedDataSource(
    private val omDbApi: OMDbApi,
    private val movieQuery: String,
    private val retryExecutor: Executor
) : PageKeyedDataSource<Int, Movie>() {

    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    // keep a request reference for the cancel event
    private var request: Call<MovieSearch>? = null

    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter.
     */
    val networkState = MutableLiveData<NetworkState>()

    val totalResults = MutableLiveData<Int?>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    fun cancelLoading() {
        if (request?.isExecuted == true && request?.isCanceled == false) {
            request?.cancel()
        } else {
            networkState.postValue(NetworkState.CANCELED)
        }
        request = null
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        val request = omDbApi.getMovies(
            query = movieQuery,
            page = FIRST_PAGE
        )
        this.request = request
        networkState.postValue(NetworkState.LOADING)
        totalResults.postValue(null)

        request.enqueue(object : Callback<MovieSearch> {
            override fun onResponse(
                call: Call<MovieSearch>,
                response: Response<MovieSearch>
            ) {
                if (response.isSuccessful) {
                    retry = null
                    val items = response.body()?.movieItems ?: emptyList()
                    networkState.postValue(NetworkState.LOADED)
                    val totalCount = try {
                        response.body()?.totalResults?.toInt() ?: 0
                    } catch (e: NumberFormatException) {
                        0
                    }
                    totalResults.postValue(totalCount)
                    callback.onResult(items, null, FIRST_PAGE + 1)
                } else {
                    retry = {
                        loadInitial(params, callback)
                    }
                    networkState.postValue(NetworkState.ERROR)
                }
            }

            override fun onFailure(call: Call<MovieSearch>, t: Throwable) {
                retry = {
                    loadInitial(params, callback)
                }
                if (call.isCanceled) {
                    networkState.postValue(NetworkState.CANCELED)
                } else {
                    networkState.postValue(NetworkState.ERROR)
                }
            }
        })
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Movie>
    ) {
        networkState.postValue(NetworkState.LOADING)
        val request = omDbApi.getMovies(
            query = movieQuery,
            page = params.key
        )
        this.request = request
        request.enqueue(object : Callback<MovieSearch> {
            override fun onResponse(
                call: Call<MovieSearch>,
                response: Response<MovieSearch>
            ) {
                if (response.isSuccessful) {
                    retry = null
                    val items = response.body()?.movieItems ?: emptyList()
                    val key = params.key + 1
                    callback.onResult(items, key)
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    retry = {
                        loadAfter(params, callback)
                    }
                    networkState.postValue(NetworkState.ERROR)
                }
            }

            override fun onFailure(call: Call<MovieSearch>, t: Throwable) {
                retry = {
                    loadAfter(params, callback)
                }
                if (call.isCanceled) {
                    networkState.postValue(NetworkState.CANCELED)
                } else {
                    networkState.postValue(NetworkState.ERROR)
                }
            }
        })
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Movie>
    ) {
        // ignored, since we only ever append to our initial load
    }
}