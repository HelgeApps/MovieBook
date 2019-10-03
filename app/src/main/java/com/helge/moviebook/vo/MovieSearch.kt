package com.helge.moviebook.vo

import com.google.gson.annotations.SerializedName

data class MovieSearch(
    @field:SerializedName("Search")
    var movieItems: List<Movie>?,
    @field:SerializedName("totalResults")
    var totalResults: String?,
    @field:SerializedName("Response")
    var response: String?
)