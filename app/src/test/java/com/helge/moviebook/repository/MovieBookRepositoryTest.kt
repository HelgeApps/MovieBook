package com.helge.moviebook.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.helge.moviebook.repository.db.RecentEntity
import com.helge.moviebook.repository.network.RemoteDataSource
import com.helge.moviebook.vo.RecentQuery
import com.jraska.livedata.test
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class MovieBookRepositoryTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val remoteDataSource = mock(RemoteDataSource::class)
    private lateinit var underTest: MovieBookRepository

    @Before
    fun setUp() {
        underTest = MovieBookRepository(TestRecentStore(), remoteDataSource)
    }

    @Test
    fun `can save query`() {
        val testQuery = RecentQuery("test query")

        underTest.apply {
            recentQueries().test().value().shouldBeEmpty()

            runBlocking { saveMovieQuery(testQuery) }

            recentQueries().test().value() shouldContainSame listOf(testQuery)
        }
    }

    @Test
    fun `can remove query`() {
        val testQuery = RecentQuery("test query")

        underTest.apply {
            recentQueries().test().value().shouldBeEmpty()

            runBlocking { saveMovieQuery(testQuery) }

            recentQueries().test().value() shouldContainSame listOf(testQuery)

            runBlocking { deleteMovieQuery(testQuery) }

            recentQueries().test().value().shouldBeEmpty()
        }
    }

    @Test
    fun `can replace query`() {
        val testQuery = RecentQuery("test query")
        val replacement = testQuery.copy(createdOn = Calendar.getInstance())

        underTest.apply {
            recentQueries().test().value().shouldBeEmpty()

            runBlocking { saveMovieQuery(testQuery) }

            recentQueries().test().value() shouldContainSame listOf(testQuery)

            runBlocking { saveMovieQuery(replacement) }

            recentQueries().test().value() shouldContainSame listOf(replacement)
        }
    }

    @Test
    fun `can save maximum 20 queries`() {
        underTest.apply {
            recentQueries().test().value().shouldBeEmpty()

            runBlocking {
                for (i in 0..RecentEntity.Store.MAX_ROWS + 5) {
                    saveMovieQuery(RecentQuery("query $i"))
                }
            }

            recentQueries().test().value().size shouldEqualTo RecentEntity.Store.MAX_ROWS
        }
    }

    class TestRecentStore : RecentEntity.Store {
        private val _items =
            MutableLiveData<List<RecentEntity>>().apply { value = listOf() }

        override fun all(): LiveData<List<RecentEntity>> = _items

        override suspend fun save(entity: RecentEntity) {
            _items.value = if (current().any { it.query == entity.query }) {
                current().map { if (it.query == entity.query) entity else it }
            } else {
                current() + entity
            }
        }

        override suspend fun limitRows() {
            if (current().size > RecentEntity.Store.MAX_ROWS) {
                _items.value =
                    current().sortedWith(compareByDescending { it.createdOn.timeInMillis })
                        .take(RecentEntity.Store.MAX_ROWS)
            }
        }

        override suspend fun delete(entity: RecentEntity) {
            _items.value = current().filter { entity.query != entity.query }
        }

        private fun current() = _items.value!!
    }
}
