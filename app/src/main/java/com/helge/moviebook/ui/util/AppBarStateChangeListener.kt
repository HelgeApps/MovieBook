package com.helge.moviebook.ui.util

import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

abstract class AppBarStateChangeListener : AppBarLayout.OnOffsetChangedListener {

    private var currentState = State.IDLE

    enum class State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
        when {
            i == 0 -> {
                if (currentState != State.EXPANDED) {
                    onStateChanged(appBarLayout,
                        State.EXPANDED
                    )
                }
                currentState = State.EXPANDED
            }
            abs(i) >= appBarLayout.totalScrollRange -> {
                if (currentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout,
                        State.COLLAPSED
                    )
                }
                currentState =
                    State.COLLAPSED
            }
            else -> {
                if (currentState != State.IDLE) {
                    onStateChanged(appBarLayout,
                        State.IDLE
                    )
                }
                currentState = State.IDLE
            }
        }
    }

    abstract fun onStateChanged(appBarLayout: AppBarLayout, state: State)
}