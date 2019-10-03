package com.helge.moviebook.ui.search

import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import com.helge.GlideApp
import com.helge.moviebook.R
import com.helge.moviebook.repository.network.Status
import com.helge.moviebook.ui.SharedViewModel
import com.helge.moviebook.vo.Movie
import kotlinx.android.synthetic.main.movies_search_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModel()
    // shared view model to get a recent query from RecentFragment
    private val sharedViewModel: SharedViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.movies_search_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recent.setOnClickListener {
            displayRecentQueries()
        }

        val adapter = SearchMoviesAdapter(
            inflater = layoutInflater,
            glide = GlideApp.with(this),
            onRetryClick = { viewModel.retry() },
            onCancelClick = { viewModel.cancel() },
            onRowClick = { movie -> displayMovieDetails(movie) }
        )

        list.apply {
            setAdapter(adapter)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        // search results
        viewModel.movies.observe(this, Observer<PagedList<Movie>> {
            adapter.submitList(it)
        })

        viewModel.networkState.observe(this, Observer {
            if (it.status != Status.SUCCESS) {
                info.visibility = View.GONE
            }
            adapter.setNetworkState(it)
        })

        viewModel.totalResults.observe(this, Observer { count ->
            when {
                // new query triggered
                count == null -> number.visibility = View.GONE
                // found 1 or more movies
                count > 0 -> {
                    number.text = resources.getQuantityString(R.plurals.movies_found, count, count)
                    number.visibility = View.VISIBLE
                    info.visibility = View.GONE
                }
                // nothing found
                else -> {
                    number.visibility = View.GONE
                    info.setText(R.string.movies_not_found)
                    info.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_view_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val queryTrimmed = query!!.trim()
                if (queryTrimmed.isNotEmpty()) {
                    viewModel.findMovie(queryTrimmed)
                    return false
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return false
            }
        })

        super.onCreateOptionsMenu(menu, inflater)

        // query from SearchFragment
        sharedViewModel.movieSearchQuery?.let { query ->
            searchView.setQuery(query, true)
            sharedViewModel.movieSearchQuery = null
        }
    }

    private fun displayRecentQueries() {
        hideKeyboard()
        findNavController().navigate(
            SearchFragmentDirections.displayRecentQueries()
        )
    }

    private fun displayMovieDetails(movie: Movie) {
        hideKeyboard()
        findNavController().navigate(
            SearchFragmentDirections.displayMovieDetails(
                movie.imdbID,
                movie.title
            )
        )
    }

    private fun hideKeyboard() {
        view?.let {
            val imm = context?.getSystemService<InputMethodManager>()

            imm?.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}