package com.helge.moviebook.ui.search

import android.content.Intent
import android.view.KeyEvent
import android.widget.EditText
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.helge.moviebook.R
import com.helge.moviebook.repository.MovieBookRepository
import com.helge.moviebook.repository.db.RecentDatabase
import com.helge.moviebook.repository.network.RemoteDataSource
import com.helge.moviebook.ui.MainActivity
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class SearchFragmentTest {
    private lateinit var repo: MovieBookRepository

    @get:Rule
    var testRule = CountingTaskExecutorRule()

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = RecentDatabase.newTestInstance(context)

        // set fake api to get responses from data source
        val remoteDataSource = RemoteDataSource(FakeOMDbApi())

        repo = MovieBookRepository(db.recentStore(), remoteDataSource)

        loadKoinModules(module {
            single(override = true) { repo }
        })
    }

    @Test
    fun testListContents() {
        // start activity and SearchFragment (home Fragment)
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val activity = InstrumentationRegistry.getInstrumentation().startActivitySync(intent)

        // open SearchView
        onView(withId(R.id.action_search)).perform(click())

        // submit test query on SearchView to get search results
        onView(isAssignableFrom(EditText::class.java)).perform(
            typeText("test"),
            pressKey(KeyEvent.KEYCODE_ENTER)
        )

        // check if RecyclerView contains the correct numbers of items
        val recyclerView = activity.findViewById<RecyclerView>(R.id.list)
        assertThat(recyclerView.adapter, CoreMatchers.notNullValue())
        waitForAdapterChange(recyclerView)
        assertThat(recyclerView.adapter?.itemCount, CoreMatchers.`is`(1))
    }

    private fun waitForAdapterChange(recyclerView: RecyclerView) {
        val latch = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            recyclerView.adapter?.registerAdapterDataObserver(
                object : RecyclerView.AdapterDataObserver() {
                    override fun onChanged() {
                        latch.countDown()
                    }

                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        latch.countDown()
                    }
                })
        }
        testRule.drainTasks(1, TimeUnit.SECONDS)
        if (recyclerView.adapter?.itemCount ?: 0 > 0) {
            return
        }
        assertThat(latch.await(10, TimeUnit.SECONDS), CoreMatchers.`is`(true))
    }
}
