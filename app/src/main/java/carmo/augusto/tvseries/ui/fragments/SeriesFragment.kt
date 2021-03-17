package carmo.augusto.tvseries.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import carmo.augusto.core.extensions.exhaustive
import carmo.augusto.core.utils.SingleEventWrapper
import carmo.augusto.core.viewmodels.AbsSeriesViewModel
import carmo.augusto.tvseries.R
import carmo.augusto.tvseries.databinding.FragmentSeriesBinding
import carmo.augusto.tvseries.extensions.toHtml
import carmo.augusto.tvseries.ui.adapters.SeasonsPagerAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.viewmodel.ext.android.viewModel

class SeriesFragment : AppBaseFragment() {

    private val viewModel by viewModel<AbsSeriesViewModel>()

    private val navArgs by navArgs<SeriesFragmentArgs>()

    private var _binding: FragmentSeriesBinding? = null
    private val binding get() = _binding!!

    private val glide by lazy { Glide.with(this) }

    private lateinit var seasonsPagerAdapter: SeasonsPagerAdapter

    private val seriesId by lazy { navArgs.seriesId }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSeriesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()

        binding.poster.transitionName = context.getString(R.string.transition_poster, seriesId)
        binding.name.transitionName = context.getString(R.string.transition_name, seriesId)
        binding.summary.transitionName = context.getString(R.string.transition_summary, seriesId)
        binding.genres.transitionName = context.getString(R.string.transition_genres, seriesId)

        seasonsPagerAdapter = SeasonsPagerAdapter(this)
        binding.pager.adapter = seasonsPagerAdapter
        binding.pager.offscreenPageLimit = SEASONS_PAGER_OFFSCREEN_LIMIT
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            val pageState = viewModel.pageState.value as? AbsSeriesViewModel.PageState.Loaded
            if (pageState == null) {
                tab.text = ""
                return@TabLayoutMediator
            }

            tab.text = requireContext().getString(
                R.string.season_format,
                pageState.series.seasons!![position].number
            )
        }.attach()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        registerObservers()

        glide.load(navArgs.posterUrl)
            .placeholder(R.drawable.ic_placeholder_poster)
            .centerCrop()
            .into(binding.poster)
        binding.name.text = navArgs.name?.toHtml()
        binding.summary.text = navArgs.summary?.toHtml()
        binding.genres.text = navArgs.genres

        viewModel.load(seriesId = navArgs.seriesId)
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

    private fun handlePageStateChange(pageState: AbsSeriesViewModel.PageState) {
        binding.info.hideAll()

        when (pageState) {
            AbsSeriesViewModel.PageState.Idle -> {

            }
            AbsSeriesViewModel.PageState.Loading -> {
                binding.info.showLoadingView()
            }
            is AbsSeriesViewModel.PageState.Loaded -> {
                val series = pageState.series

                glide.load(series.posterImage?.mediumUrl)
                    .placeholder(R.drawable.ic_placeholder_poster)
                    .centerCrop()
                    .into(binding.poster)
                binding.name.text = series.name.toHtml()
                binding.time.text = series.schedule.time
                binding.days.text = series.schedule.days.joinToString(separator = " • ")
                binding.summary.text = series.summary?.toHtml()
                binding.genres.text = series.genres.joinToString(separator = " • ")

                seasonsPagerAdapter.setSeasons(series.seasons ?: emptyList())
            }
            AbsSeriesViewModel.PageState.Error.Generic -> {
                binding.info.showGenericErrorInfoView {
                    viewModel.load(seriesId = seriesId)
                }
            }
            AbsSeriesViewModel.PageState.Error.NoInternet -> {
                binding.info.showNoInternetInfoView {
                    viewModel.load(seriesId = seriesId)
                }
            }
            AbsSeriesViewModel.PageState.Error.Server -> {
                binding.info.showServerErrorInfoView {
                    viewModel.load(seriesId = seriesId)
                }
            }
        }.exhaustive
    }

    private fun handleNavRequestEvent(
        event: SingleEventWrapper<AbsSeriesViewModel.NavigationRequest>,
    ) {
        val navRequest = event.getDataIfNotHandled()
        if (navRequest == null) {
            return
        }

        when (navRequest) {
            is AbsSeriesViewModel.NavigationRequest.Episode -> {

            }
        }.exhaustive
    }

    private fun handleMessageEvent(event: SingleEventWrapper<AbsSeriesViewModel.Message>) {
        val message = event.getDataIfNotHandled()
        if (message == null) {
            return
        }

        val messageTest = when (message) {
            AbsSeriesViewModel.Message.LoadingError.Generic -> {
                getString(R.string.toast_generic_error)
            }
            AbsSeriesViewModel.Message.LoadingError.NoInternet -> {
                getString(R.string.toast_no_internet_connection)
            }
            AbsSeriesViewModel.Message.LoadingError.Server -> {
                getString(R.string.toast_server_error)
            }
        }

        Toast.makeText(requireContext(), messageTest, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val SEASONS_PAGER_OFFSCREEN_LIMIT = 2
    }
}