package com.helge.moviebook.ui.recent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.helge.moviebook.R
import com.helge.moviebook.vo.RecentQuery
import kotlinx.android.synthetic.main.recent_queries_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.recyclerview.widget.ItemTouchHelper

class RecentFragment : Fragment() {
    private val viewModel: RecentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.recent_queries_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter =
            RecentQueriesAdapter(
                inflater = layoutInflater,
                onRowClick = { model -> searchMovie(model) }
            )

        list.apply {
            setAdapter(adapter)
            addItemDecoration(
                DividerItemDecoration(
                    activity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    viewModel.deleteMovieQuery(position)
                }
            }
        )
        itemTouchHelper.attachToRecyclerView(list)

        viewModel.recentQueries.observe(this, Observer { items ->
            adapter.submitList(items)

            if (items.isEmpty()) {
                empty.visibility = View.VISIBLE
            } else {
                empty.visibility = View.GONE
            }

            loading.visibility = View.GONE
        })
    }

    /**
     * Get back to SearchFragment to repeat the request
     */
    private fun searchMovie(model: RecentQuery) {
        findNavController().navigate(
            RecentFragmentDirections.doSearchRequest(model.query)
        )
    }
}