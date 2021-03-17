package carmo.augusto.tvseries.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import carmo.augusto.core.entities.Series
import carmo.augusto.core.extensions.exhaustive
import carmo.augusto.core.utils.SingleEventWrapper
import carmo.augusto.core.viewmodels.AbsSearchSeriesViewModel
import carmo.augusto.tvseries.R
import carmo.augusto.tvseries.databinding.FragmentSearchSeriesBinding
import carmo.augusto.tvseries.ui.adapters.SeriesListAdapter
import carmo.augusto.tvseries.ui.decoration.MarginItemDecoration
import com.bumptech.glide.Glide
import org.koin.android.viewmodel.ext.android.viewModel

class SearchSeriesFragment : AppBaseFragment() {

    private val viewModel by viewModel<AbsSearchSeriesViewModel>()

    private var _binding: FragmentSearchSeriesBinding? = null
    private val binding get() = _binding!!

    private val glide by lazy { Glide.with(this) }

    private lateinit var seriesListAdapter: SeriesListAdapter

    private val seriesListAdapterListener = object : SeriesListAdapter.Listener {
        override fun onSeriesCardCLicked(series: Series) {
            viewModel.onSeriesClicked(series = series)
        }

        override fun onLoadMoreButtonClicked() {
            // do nothing
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchSeriesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        registerObservers()

        seriesListAdapter = SeriesListAdapter(glide = glide)
        seriesListAdapter.listener = seriesListAdapterListener

        val defaultMargin = resources.getDimensionPixelSize(R.dimen.default_margin)
        val defaultHalfMargin = resources.getDimensionPixelSize(R.dimen.default_half_margin)

        binding.list.setHasFixedSize(true)
        seriesListAdapter.loadingMoreButtonState = SeriesListAdapter.LoadMoreButtonState.HIDDEN
        binding.list.adapter = seriesListAdapter
        binding.list.addItemDecoration(
            MarginItemDecoration(
                marginTop = defaultHalfMargin,
                marginRight = defaultMargin,
                marginBottom = defaultHalfMargin,
                marginLeft = defaultMargin
            )
        )

        binding.searchField.doOnTextChanged { text, _, _, _ ->
            viewModel.search(query = text?.toString() ?: "")
        }

        showSoftKeyboard(binding.searchField)

        // fix shared transition return animation
        postponeEnterTransition()
        binding.list.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    override fun onStop() {
        hideSoftKeyboard()

        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun registerObservers() {
        viewModel.pageState.observe(viewLifecycleOwner, ::handlePageStateChange)
        viewModel.navRequest.observe(viewLifecycleOwner, ::handleNavRequestEvent)
        viewModel.message.observe(viewLifecycleOwner, ::handleMessageEvent)
    }

    private fun handlePageStateChange(pageState: AbsSearchSeriesViewModel.PageState) {
        binding.info.hideAll()

        when (pageState) {
            AbsSearchSeriesViewModel.PageState.Idle -> {

            }
            is AbsSearchSeriesViewModel.PageState.Searching -> {
                binding.info.showLoadingView()
            }
            is AbsSearchSeriesViewModel.PageState.Loaded -> {
                seriesListAdapter.setData(pageState.seriesList)
            }
            AbsSearchSeriesViewModel.PageState.Error.Generic -> {
                binding.info.showGenericErrorInfoView {
                    viewModel.redoLastSearch()
                }
            }
            AbsSearchSeriesViewModel.PageState.Error.NoInternet -> {
                binding.info.showNoInternetInfoView {
                    viewModel.redoLastSearch()
                }
            }
            AbsSearchSeriesViewModel.PageState.Error.Server -> {
                binding.info.showServerErrorInfoView {
                    viewModel.redoLastSearch()
                }
            }
        }.exhaustive
    }

    private fun handleNavRequestEvent(event: SingleEventWrapper<AbsSearchSeriesViewModel.NavigationRequest>) {
        val navRequest = event.getDataIfNotHandled()
        if (navRequest == null) {
            return
        }

        when (navRequest) {
            is AbsSearchSeriesViewModel.NavigationRequest.Series -> {
                val series = navRequest.series
                val seriesAdapterPosition = seriesListAdapter.findSeriesAdapterPosition(
                    seriesId = series.id
                )
                val viewHolder = binding.list
                    .findViewHolderForAdapterPosition(seriesAdapterPosition)
                        as? SeriesListAdapter.SeriesViewHolder

                val directions =
                    SearchSeriesFragmentDirections.actionSearchSeriesFragmentToSeriesFragment(
                        seriesId = series.id,
                        posterUrl = series.posterImage?.mediumUrl,
                        name = series.name,
                        summary = series.summary,
                        genres = series.genres.joinToString(separator = " • ")
                    )
                val extras = if (viewHolder != null) {
                    FragmentNavigatorExtras(
                        viewHolder.binding.poster to viewHolder.binding.poster.transitionName,
                        viewHolder.binding.name to viewHolder.binding.name.transitionName,
                        viewHolder.binding.summary to viewHolder.binding.summary.transitionName,
                        viewHolder.binding.genres to viewHolder.binding.genres.transitionName
                    )
                } else {
                    FragmentNavigatorExtras()
                }

                findNavController().navigate(directions, extras)
            }
        }.exhaustive
    }

    private fun handleMessageEvent(event: SingleEventWrapper<AbsSearchSeriesViewModel.Message>) {
        val message = event.getDataIfNotHandled()
        if (message == null) {
            return
        }

        val messageTest = when (message) {
            AbsSearchSeriesViewModel.Message.LoadingError.Generic -> {
                getString(R.string.toast_generic_error)
            }
            AbsSearchSeriesViewModel.Message.LoadingError.NoInternet -> {
                getString(R.string.toast_no_internet_connection)
            }
            AbsSearchSeriesViewModel.Message.LoadingError.Server -> {
                getString(R.string.toast_server_error)
            }
        }

        Toast.makeText(requireContext(), messageTest, Toast.LENGTH_SHORT).show()
    }
}