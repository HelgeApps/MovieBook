package com.helge.moviebook.vo

import com.google.gson.annotations.SerializedName

data class Movie(
    @field:SerializedName("Title")
    var title: String,
    @field:SerializedName("Year")
    var year: String?,
    @field:SerializedName("imdbID")
    var imdbID: String,
    @field:SerializedName("Type")
    var type: String?,
    @field:SerializedName("Poster")
    var poster: String?
)
