package com.helge.moviebook.ui.util

import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

object Utils {
    fun unlockAppBarOpen(appBarLayout: AppBarLayout) {
        setAppBarDragging(appBarLayout, true)
        appBarLayout.setExpanded(true)
    }

    fun lockAppBarClosed(appBarLayout: AppBarLayout) {
        appBarLayout.setExpanded(false, false)
        setAppBarDragging(appBarLayout, false)
    }

    private fun setAppBarDragging(appBarLayout: AppBarLayout, isEnabled: Boolean) {
        val params =
            appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = AppBarLayout.Behavior()
        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return isEnabled
            }
        })
        params.behavior = behavior
    }
}