package com.helge.moviebook.repository.network

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED,
    CANCELED
}

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(val status: Status) {
    companion object {
        val LOADED =
            NetworkState(Status.SUCCESS)
        val LOADING =
            NetworkState(Status.RUNNING)
        val ERROR =
            NetworkState(Status.FAILED)
        val CANCELED =
            NetworkState(Status.CANCELED)
    }
}