package carmo.augusto.tvseries.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import carmo.augusto.core.extensions.exhaustive
import carmo.augusto.core.utils.SingleEventWrapper
import carmo.augusto.core.viewmodels.AbsEpisodeViewModel
import carmo.augusto.tvseries.R
import carmo.augusto.tvseries.databinding.FragmentEpisodeBinding
import carmo.augusto.tvseries.extensions.toHtml
import com.bumptech.glide.Glide
import org.koin.android.viewmodel.ext.android.viewModel

class EpisodeFragment : AppBaseFragment() {

    private val viewModel by viewModel<AbsEpisodeViewModel>()

    private var _binding: FragmentEpisodeBinding? = null
    private val binding get() = _binding!!

    private val navArgs by navArgs<EpisodeFragmentArgs>()

    private val glide by lazy { Glide.with(this) }

    private val episodeId by lazy { navArgs.episodeId }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        registerObservers()

        viewModel.loadEpisode(episodeId = episodeId)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun registerObservers() {
        viewModel.pageState.observe(viewLifecycleOwner, ::handlePageStateChange)
        viewModel.message.observe(viewLifecycleOwner, ::handleMessageEvent)
    }

    private fun handlePageStateChange(pageState: AbsEpisodeViewModel.PageState) {
        binding.info.hideAll()

        when (pageState) {
            AbsEpisodeViewModel.PageState.Idle -> {

            }
            AbsEpisodeViewModel.PageState.Loading -> {
                binding.info.showLoadingView()
            }
            is AbsEpisodeViewModel.PageState.Loaded -> {
                val episode = pageState.episode

                glide.load(episode.image?.originalUrl)
                    .placeholder(R.drawable.ic_placeholder_poster)
                    .centerCrop()
                    .into(binding.poster)
                binding.name.text = episode.name.toHtml()
                binding.seasonAndEpisode.text = getString(
                    R.string.season_and_episode_format,
                    episode.season,
                    episode.number
                )
                binding.summary.text = episode.summary?.toHtml()
            }
            AbsEpisodeViewModel.PageState.Error.Generic -> {
                binding.info.showGenericErrorInfoView {
                    viewModel.loadEpisode(episodeId = episodeId)
                }
            }
            AbsEpisodeViewModel.PageState.Error.NoInternet -> {
                binding.info.showNoInternetInfoView {
                    viewModel.loadEpisode(episodeId = episodeId)
                }
            }
            AbsEpisodeViewModel.PageState.Error.Server -> {
                binding.info.showServerErrorInfoView {
                    viewModel.loadEpisode(episodeId = episodeId)
                }
            }
        }.exhaustive
    }

    private fun handleMessageEvent(event: SingleEventWrapper<AbsEpisodeViewModel.Message>) {
        val message = event.getDataIfNotHandled()
        if (message == null) {
            return
        }

        val messageTest = when (message) {
            AbsEpisodeViewModel.Message.LoadingError.Generic -> {
                getString(R.string.toast_generic_error)
            }
            AbsEpisodeViewModel.Message.LoadingError.NoInternet -> {
                getString(R.string.toast_no_internet_connection)
            }
            AbsEpisodeViewModel.Message.LoadingError.Server -> {
                getString(R.string.toast_server_error)
            }
        }

        Toast.makeText(requireContext(), messageTest, Toast.LENGTH_SHORT).show()
    }
}