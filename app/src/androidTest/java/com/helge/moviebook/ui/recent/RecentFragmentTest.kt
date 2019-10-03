package com.helge.moviebook.ui.recent

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.helge.moviebook.R
import com.helge.moviebook.api.OMDbApi
import com.helge.moviebook.repository.MovieBookRepository
import com.helge.moviebook.repository.db.RecentDatabase
import com.helge.moviebook.repository.network.RemoteDataSource
import com.helge.moviebook.ui.MainActivity
import com.helge.moviebook.vo.RecentQuery
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class RecentFragmentTest {
    private lateinit var repo: MovieBookRepository
    private val items = listOf(
        RecentQuery("Query #1"),
        RecentQuery("Query #2")
    )

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = RecentDatabase.newTestInstance(context)

        repo = MovieBookRepository(db.recentStore(), RemoteDataSource(OMDbApi.create()))

        loadKoinModules(module {
            single(override = true) { repo }
        })

        // save items to database
        runBlocking { items.forEach { repo.saveMovieQuery(it) } }
    }

    @Test
    fun testListContents() {
        // start activity and SearchFragment (home Fragment)
        ActivityScenario.launch(MainActivity::class.java)

        // click the recent button to open RecentFragment
        onView(withId(R.id.recent)).perform(click())

        // check if RecyclerView contains the correct numbers of items
        onView(withId(R.id.list)).check(matches(hasChildCount(items.size)))
    }
}
