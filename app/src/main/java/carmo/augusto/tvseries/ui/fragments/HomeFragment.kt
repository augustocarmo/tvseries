package carmo.augusto.tvseries.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import carmo.augusto.core.entities.Series
import carmo.augusto.core.extensions.exhaustive
import carmo.augusto.core.utils.SingleEventWrapper
import carmo.augusto.core.viewmodels.AbsHomeViewModel
import carmo.augusto.tvseries.R
import carmo.augusto.tvseries.databinding.FragmentHomeBinding
import carmo.augusto.tvseries.ui.adapters.SeriesListAdapter
import carmo.augusto.tvseries.ui.adapters.SeriesListAdapter.*
import carmo.augusto.tvseries.ui.decoration.MarginItemDecoration
import com.bumptech.glide.Glide
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : AppBaseFragment() {

    private val viewModel by viewModel<AbsHomeViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val glide by lazy { Glide.with(this) }

    private lateinit var seriesListAdapter: SeriesListAdapter

    private val seriesListAdapterListener = object : Listener {
        override fun onSeriesCardCLicked(series: Series) {
            viewModel.onSeriesClicked(series = series)
        }

        override fun onLoadMoreButtonClicked() {
            viewModel.onLoadMoreButtonClicked()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        registerObservers()

        seriesListAdapter = SeriesListAdapter(glide = glide)
        seriesListAdapter.listener = seriesListAdapterListener

        val defaultMargin = resources.getDimensionPixelSize(R.dimen.default_margin)
        val defaultHalfMargin = resources.getDimensionPixelSize(R.dimen.default_half_margin)

        binding.list.setHasFixedSize(true)
        binding.list.adapter = seriesListAdapter
        binding.list.addItemDecoration(
            MarginItemDecoration(
                marginTop = defaultHalfMargin,
                marginRight = defaultMargin,
                marginBottom = defaultHalfMargin,
                marginLeft = defaultMargin
            )
        )

        // fix shared transition return animation
        postponeEnterTransition()
        binding.list.doOnPreDraw {
            startPostponedEnterTransition()
        }

        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                viewModel.onSearchButtonCLicked()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun registerObservers() {
        viewModel.pageState.observe(viewLifecycleOwner, ::handlePageStateChange)
        viewModel.navRequest.observe(viewLifecycleOwner, ::handleNavRequestEvent)
        viewModel.message.observe(viewLifecycleOwner, ::handleMessageEvent)
    }

    private fun handlePageStateChange(pageState: AbsHomeViewModel.PageState) {
        binding.info.hideAll()

        when (pageState) {
            AbsHomeViewModel.PageState.Idle -> {

            }
            AbsHomeViewModel.PageState.Loading -> {
                binding.info.showLoadingView()
            }
            is AbsHomeViewModel.PageState.Loaded.Loaded -> {
                seriesListAdapter.setData(pageState.series)
                seriesListAdapter.loadingMoreButtonState = if (pageState.canLoadMore) {
                    LoadMoreButtonState.VISIBLE
                } else {
                    LoadMoreButtonState.HIDDEN
                }
            }
            is AbsHomeViewModel.PageState.Loaded.LoadingMore -> {
                seriesListAdapter.loadingMoreButtonState = LoadMoreButtonState.LOADING
            }
            AbsHomeViewModel.PageState.Error.Generic -> {
                binding.info.showGenericErrorInfoView {
                    viewModel.load()
                }
            }
            AbsHomeViewModel.PageState.Error.NoInternet -> {
                binding.info.showNoInternetInfoView {
                    viewModel.load()
                }
            }
            AbsHomeViewModel.PageState.Error.Server -> {
                binding.info.showServerErrorInfoView {
                    viewModel.load()
                }
            }
        }.exhaustive
    }

    private fun handleNavRequestEvent(event: SingleEventWrapper<AbsHomeViewModel.NavigationRequest>) {
        val navRequest = event.getDataIfNotHandled()
        if (navRequest == null) {
            return
        }

        when (navRequest) {
            is AbsHomeViewModel.NavigationRequest.Series -> {
                val series = navRequest.series
                val seriesAdapterPosition = seriesListAdapter.findSeriesAdapterPosition(
                    seriesId = series.id
                )
                val viewHolder = binding.list
                    .findViewHolderForAdapterPosition(seriesAdapterPosition)
                        as? SeriesViewHolder

                val directions = HomeFragmentDirections.actionHomeFragmentToSeriesFragment(
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
            AbsHomeViewModel.NavigationRequest.SearchSeries -> {
                val directions = HomeFragmentDirections.actionHomeFragmentToSearchSeriesFragment()

                findNavController().navigate(directions)
            }
        }.exhaustive
    }

    private fun handleMessageEvent(event: SingleEventWrapper<AbsHomeViewModel.Message>) {
        val message = event.getDataIfNotHandled()
        if (message == null) {
            return
        }

        val messageTest = when (message) {
            AbsHomeViewModel.Message.LoadingError.Generic -> {
                getString(R.string.toast_generic_error)
            }
            AbsHomeViewModel.Message.LoadingError.NoInternet -> {
                getString(R.string.toast_no_internet_connection)
            }
            AbsHomeViewModel.Message.LoadingError.Server -> {
                getString(R.string.toast_server_error)
            }
            AbsHomeViewModel.Message.LoadingError.AllSeriesAlreadyLoaded -> {
                getString(R.string.toast_all_series_already_loaded)
            }
        }

        Toast.makeText(requireContext(), messageTest, Toast.LENGTH_SHORT).show()
    }
}