package com.helge.moviebook.vo

import java.util.*

data class RecentQuery(
    val query: String,
    val createdOn: Calendar = Calendar.getInstance()
)