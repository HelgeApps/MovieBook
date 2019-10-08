package com.helge.moviebook

import android.app.Application
import com.helge.moviebook.api.OMDbApi
import com.helge.moviebook.repository.MovieBookRepository
import com.helge.moviebook.repository.db.RecentDatabase
import com.helge.moviebook.repository.network.RemoteDataSource
import com.helge.moviebook.ui.details.DetailsViewModel
import com.helge.moviebook.ui.recent.RecentViewModel
import com.helge.moviebook.ui.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MovieBookApp : Application() {
    private val koinModule = module {
        single { OMDbApi.create() }
        single { RemoteDataSource(get()) }
        single { RecentDatabase.newInstance(androidContext()) }
        single {
            val db: RecentDatabase = get()
            MovieBookRepository(db.recentStore(), get())
        }
        viewModel { SearchViewModel(get()) }
        viewModel { DetailsViewModel(get()) }
        viewModel { RecentViewModel(get()) }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Android context
            androidContext(this@MovieBookApp)
            // modules
            modules(koinModule)
        }
    }
}