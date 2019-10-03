package com.helge.moviebook.repository.network

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.helge.moviebook.api.OMDbApi
import com.helge.moviebook.vo.Movie
import java.util.concurrent.Executor

/**
 * A data source factory which provides a way to observe the last created data source.
 * This allows us to channel its network request status etc back to the UI. See the Listing creation
 * in the RemoteDataSource class.
 */
class RemoteDataSourceFactory(
    private val omDbApi: OMDbApi,
    private val movieQuery: String,
    private val retryExecutor: Executor
) : DataSource.Factory<Int, Movie>() {
    val sourceLiveData = MutableLiveData<RemotePageKeyedDataSource>()
    override fun create(): DataSource<Int, Movie> {
        val source = RemotePageKeyedDataSource(
            omDbApi,
            movieQuery,
            retryExecutor
        )
        sourceLiveData.postValue(source)
        return source
    }
}
