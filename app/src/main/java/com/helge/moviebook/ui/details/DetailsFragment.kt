package com.helge.moviebook.ui.details

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import com.helge.GlideApp
import com.helge.GlideOptions.bitmapTransform
import com.helge.moviebook.R
import com.helge.moviebook.databinding.MovieDetailsFragmentBinding
import com.helge.moviebook.ui.util.AppBarStateChangeListener
import com.helge.moviebook.ui.util.Utils
import jp.wasabeef.glide.transformations.BlurTransformation
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsFragment : Fragment() {
    private lateinit var binding: MovieDetailsFragmentBinding
    private val args: DetailsFragmentArgs by navArgs()
    private val viewModel: DetailsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = MovieDetailsFragmentBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // toolbar is collapsed and locked until the poster is loaded
        Utils.lockAppBarClosed(binding.appBarLayout)

        binding.toolbar.title = args.movieTitle
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.loadingView.retry.setOnClickListener { viewModel.retry() }

        binding.loadingView.cancel.setOnClickListener { viewModel.cancel() }

        binding.plotShowHide.setOnClickListener {
            if (binding.plotText.isExpanded) {
                binding.plotText.collapse()
                binding.plotShowHide.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_add, 0, 0, 0
                )
            } else {
                binding.plotText.expand()
                binding.plotShowHide.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_remove, 0, 0, 0
                )
            }
        }

        binding.appBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                when (state) {
                    State.COLLAPSED -> binding.toolbar.title = args.movieTitle
                    State.EXPANDED -> binding.toolbar.title = null
                    State.IDLE -> return
                }
            }
        })

        viewModel.movieDetails.observe(this, Observer { details ->
            binding.model = details

            // set base info (Production, Country, Released)
            var baseInfo: String? = null
            if (isDataOk(details.production)) {
                baseInfo = details.production
            }
            if (isDataOk(details.country)) {
                if (baseInfo == null) {
                    baseInfo = details.country
                } else {
                    baseInfo += ", ${details.country}"
                }
            }
            if (isDataOk(details.released)) {
                val released = getString(R.string.released_date, details.released)
                if (baseInfo == null) {
                    baseInfo = released
                } else {
                    baseInfo += ". $released"
                }
            }
            baseInfo?.let {
                binding.baseInfo.text = it
                binding.baseInfo.visibility = View.VISIBLE
            }

            // show crew table if some info is available
            if (isDataOk(details.director) ||
                isDataOk(details.writer) ||
                isDataOk(details.actors)
            ) {
                binding.crewText.visibility = View.VISIBLE
                binding.crewTable.visibility = View.VISIBLE
            }

            // fill reception table dynamically 'cause size of details.ratings list can be different
            details.ratings?.let {
                for (rating in it) {
                    // list can be not empty but still containing 'N\A' values
                    if (isDataOk(rating.value)) {
                        if (binding.ratingsText.visibility != View.VISIBLE) {
                            binding.ratingsText.visibility = View.VISIBLE
                        }
                        if (binding.ratingTable.visibility != View.VISIBLE) {
                            binding.ratingTable.visibility = View.VISIBLE
                        }

                        val tableRow = layoutInflater.inflate(
                            R.layout.table_row,
                            binding.ratingTable,
                            false
                        ) as TableRow

                        val cellSource = layoutInflater.inflate(
                            R.layout.table_cell,
                            tableRow,
                            false
                        ) as TextView
                        cellSource.text = rating.source

                        val cellValue = layoutInflater.inflate(
                            R.layout.table_cell,
                            tableRow,
                            false
                        ) as TextView
                        cellValue.text = rating.value

                        tableRow.addView(cellSource)
                        tableRow.addView(cellValue)
                        binding.ratingTable.addView(tableRow)
                    }
                }
            }

            if (isDataOk(details.poster)) {
                val glide = GlideApp.with(this)
                glide.load(details.poster)
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            // height of a poster is usually much bigger than width
                            // so display blur background instead of using fitXY or centerCrop
                            glide.load(resource)
                                .apply(bitmapTransform(BlurTransformation(25)))
                                .into(binding.posterBackground)
                            // unlock and expand toolbar when the poster is loaded
                            Utils.unlockAppBarOpen(binding.appBarLayout)
                            return false
                        }
                    })
                    .into(binding.poster)
            }
        })

        viewModel.networkState.observe(this, Observer {
            binding.networkState = it
            binding.loadingView.networkState = it
        })

        viewModel.getMovieDetails(args.imdbID)
    }

    /**
     * many properties can be unavailable for some movies (server returns N/A or null)
     */
    private fun isDataOk(data: String?): Boolean {
        return data != null && data != "N/A"
    }
}