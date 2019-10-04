package com.helge.moviebook.vo

import com.google.gson.annotations.SerializedName

data class MovieDetails(
    @field:SerializedName("Title")
    var title: String? = null,
    @field:SerializedName("Year")
    var year: String? = null,
    @field:SerializedName("Rated")
    var rated: String? = null,
    @field:SerializedName("Released")
    var released: String? = null,
    @field:SerializedName("Runtime")
    var runtime: String? = null,
    @field:SerializedName("Genre")
    var genre: String? = null,
    @field:SerializedName("Director")
    var director: String? = null,
    @field:SerializedName("Writer")
    var writer: String? = null,
    @field:SerializedName("Actors")
    var actors: String? = null,
    @field:SerializedName("Plot")
    var plot: String? = null,
    @field:SerializedName("Language")
    var language: String? = null,
    @field:SerializedName("Country")
    var country: String? = null,
    @field:SerializedName("Awards")
    var awards: String? = null,
    @field:SerializedName("Poster")
    var poster: String? = null,
    @field:SerializedName("Ratings")
    var ratings: List<MovieRating>? = null,
    @field:SerializedName("Metascore")
    var metaScore: String? = null,
    @field:SerializedName("imdbRating")
    var imdbRating: String? = null,
    @field:SerializedName("imdbVotes")
    var imdbVotes: String? = null,
    @field:SerializedName("imdbID")
    var imdbID: String? = null,
    @field:SerializedName("Type")
    var type: String? = null,
    @field:SerializedName("DVD")
    var dvd: String? = null,
    @field:SerializedName("BoxOffice")
    var boxOffice: String? = null,
    @field:SerializedName("Production")
    var production: String? = null,
    @field:SerializedName("Website")
    var website: String? = null,
    @field:SerializedName("Response")
    var response: String? = null
)

data class MovieRating(
    @field:SerializedName("Source")
    var source: String?,
    @field:SerializedName("Value")
    var value: String?
)