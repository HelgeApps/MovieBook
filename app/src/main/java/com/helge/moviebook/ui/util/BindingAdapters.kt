package com.helge.moviebook.ui.util

import android.text.format.DateUtils
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.util.*

private const val NA = "N/A" // data isn't available

@BindingAdapter("formattedDate")
fun TextView.formattedDate(date: Calendar?) {
    date?.let {
        text = DateUtils.getRelativeDateTimeString(
            context,
            date.timeInMillis,
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.WEEK_IN_MILLIS,
            0
        )
    }
}

// If we want to use data binding to set our data from server we have to show/hide some views based
// on returned data or network status

@BindingAdapter("setTextOrHide")
fun TextView.setTextOrHide(text: String?) {
    if (text == null || text == NA) {
        visibility = View.GONE
    } else {
        this.text = text
        visibility = View.VISIBLE
    }
}

@BindingAdapter("hideIfNotAvailable")
fun View.hideIfNotAvailable(text: String?) {
    visibility = if (text == null || text ==NA) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("visibility")
fun View.visibility(show: Boolean) {
    visibility = if (show) {
        View.VISIBLE
    } else {
        View.GONE
    }
}
