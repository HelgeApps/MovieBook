package com.helge.moviebook.vo

import com.google.gson.annotations.SerializedName

data class MovieDetails(
    @field:SerializedName("Title")
    var title: String?,
    @field:SerializedName("Year")
    var year: String?,
    @field:SerializedName("Rated")
    var rated: String?,
    @field:SerializedName("Released")
    var released: String?,
    @field:SerializedName("Runtime")
    var runtime: String?,
    @field:SerializedName("Genre")
    var genre: String?,
    @field:SerializedName("Director")
    var director: String?,
    @field:SerializedName("Writer")
    var writer: String?,
    @field:SerializedName("Actors")
    var actors: String?,
    @field:SerializedName("Plot")
    var plot: String?,
    @field:SerializedName("Language")
    var language: String?,
    @field:SerializedName("Country")
    var country: String?,
    @field:SerializedName("Awards")
    var awards: String?,
    @field:SerializedName("Poster")
    var poster: String?,
    @field:SerializedName("Ratings")
    var ratings: List<MovieRating>?,
    @field:SerializedName("Metascore")
    var metaScore: String?,
    @field:SerializedName("imdbRating")
    var imdbRating: String?,
    @field:SerializedName("imdbVotes")
    var imdbVotes: String?,
    @field:SerializedName("imdbID")
    var imdbID: String?,
    @field:SerializedName("Type")
    var type: String?,
    @field:SerializedName("DVD")
    var dvd: String?,
    @field:SerializedName("BoxOffice")
    var boxOffice: String?,
    @field:SerializedName("Production")
    var production: String?,
    @field:SerializedName("Website")
    var website: String?,
    @field:SerializedName("Response")
    var response: String?
)

data class MovieRating(
    @field:SerializedName("Source")
    var source: String?,
    @field:SerializedName("Value")
    var value: String?
)