package com.helge.moviebook.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.helge.moviebook.R
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        setSupportActionBar(toolbar)

        findNavController(R.id.nav_host).let { nav ->
            appBarConfiguration = AppBarConfiguration(nav.graph)
            setupActionBarWithNavController(nav, appBarConfiguration)
            nav.addOnDestinationChangedListener { _, destination, _ ->
                // hide activity toolbar in Details Fragment `cause it adds its own (collapsible one)
                if (destination.id == R.id.detailsFragment) {
                    toolbar.visibility = View.GONE
                } else {
                    toolbar.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onSupportNavigateUp() =
        navigateUp(findNavController(R.id.nav_host), appBarConfiguration)
}
