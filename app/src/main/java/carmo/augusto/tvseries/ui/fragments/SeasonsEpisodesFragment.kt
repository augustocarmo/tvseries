package carmo.augusto.tvseries.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import carmo.augusto.core.extensions.exhaustive
import carmo.augusto.core.utils.SingleEventWrapper
import carmo.augusto.core.viewmodels.AbsSeasonEpisodesViewModel
import carmo.augusto.tvseries.R
import carmo.augusto.tvseries.databinding.FragmentSeasonsEpisodesBinding
import carmo.augusto.tvseries.ui.adapters.SeasonAdapter
import org.koin.android.viewmodel.ext.android.viewModel

class SeasonsEpisodesFragment : Fragment() {

    private val viewModel by viewModel<AbsSeasonEpisodesViewModel>()

    private var _binding: FragmentSeasonsEpisodesBinding? = null
    private val binding get() = _binding!!

    private val seasonId by lazy { requireArguments().getInt(BUNDLE_KEY_SEASON_ID) }

    private val seasonEpisodesAdapter = SeasonAdapter()

    private val seasonEpisodesListener = object : SeasonAdapter.Listener {
        override fun onSeasonEpisodeClicked(episodeId: Int) {
            viewModel.onEpisodeClicked(episodeId = episodeId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSeasonsEpisodesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        registerObservers()

        seasonEpisodesAdapter.listener = seasonEpisodesListener
        binding.list.setHasFixedSize(true)
        binding.list.adapter = seasonEpisodesAdapter

        viewModel.load(seasonId = seasonId)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun registerObservers() {
        viewModel.pageState.observe(viewLifecycleOwner, ::handlePageStateChange)
        viewModel.navRequest.observe(viewLifecycleOwner, ::handleNavRequestEvent)
    }

    private fun handlePageStateChange(pageState: AbsSeasonEpisodesViewModel.PageState) {
        binding.info.hideAll()

        when (pageState) {
            AbsSeasonEpisodesViewModel.PageState.Idle -> {

            }
            AbsSeasonEpisodesViewModel.PageState.Loading -> {
                binding.info.showLoadingView()
            }
            is AbsSeasonEpisodesViewModel.PageState.Loaded -> {
                seasonEpisodesAdapter.setEpisodes(pageState.episodes)
            }
            AbsSeasonEpisodesViewModel.PageState.Error.Generic -> {
                binding.info.showGenericErrorInfoView {
                    viewModel.load(seasonId = seasonId)
                }
            }
            AbsSeasonEpisodesViewModel.PageState.Error.NoInternet -> {
                binding.info.showNoInternetInfoView {
                    viewModel.load(seasonId = seasonId)
                }
            }
            AbsSeasonEpisodesViewModel.PageState.Error.Server -> {
                binding.info.showServerErrorInfoView {
                    viewModel.load(seasonId = seasonId)
                }
            }
        }.exhaustive
    }

    private fun handleNavRequestEvent(
        event: SingleEventWrapper<AbsSeasonEpisodesViewModel.NavigationRequest>
    ) {
        val navRequest = event.getDataIfNotHandled()
        if (navRequest == null) {
            return
        }

        when (navRequest) {
            is AbsSeasonEpisodesViewModel.NavigationRequest.Episode -> {
                val bundle = EpisodeFragmentArgs(
                    episodeId = navRequest.episodeId,
                    name = null
                ).toBundle()

                findNavController().navigate(
                    R.id.episodeFragment,
                    bundle
                )
            }
        }.exhaustive
    }

    companion object {
        private const val BUNDLE_KEY_SEASON_ID = "bk_season_id"

        fun create(seasonId: Int): SeasonsEpisodesFragment {
            return SeasonsEpisodesFragment().apply {
                this.arguments = Bundle().apply {
                    this.putInt(BUNDLE_KEY_SEASON_ID, seasonId)
                }
            }
        }
    }
}