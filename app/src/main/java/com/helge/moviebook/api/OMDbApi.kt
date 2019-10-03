package com.helge.moviebook.api

import android.util.Log
import com.helge.moviebook.BuildConfig
import com.helge.moviebook.vo.MovieDetails
import com.helge.moviebook.vo.MovieSearch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * OMDb API setup
 */
interface OMDbApi {
    @GET(".")
    fun getMovies(
        @Query("s") query: String,
        @Query("page") page: Int
    ): Call<MovieSearch>

    @GET(".")
    fun getMovieDetails(@Query("i") imdbID: String): Call<MovieDetails>

    companion object {
        private const val TAG = "OMDbApi"
        private const val BASE_URL = "https://www.omdbapi.com/"
        private const val API_KEY_PARAM = "apikey"
        private const val API_KEY_VALUE = "97830960"

        fun create(): OMDbApi {
            val httpUrl = BASE_URL.toHttpUrlOrNull()!!
            val clientBuilder = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val originalHttpUrl = original.url

                    val url = originalHttpUrl.newBuilder()
                        .addQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                        .build()

                    // Request customization: add request headers
                    val requestBuilder = original.newBuilder()
                        .url(url)

                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
            if (BuildConfig.DEBUG) {
                val logger = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) {
                        Log.d(TAG, message)
                    }
                })
                logger.level = HttpLoggingInterceptor.Level.BASIC
                clientBuilder.addInterceptor(logger)
            }
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OMDbApi::class.java)
        }
    }
}