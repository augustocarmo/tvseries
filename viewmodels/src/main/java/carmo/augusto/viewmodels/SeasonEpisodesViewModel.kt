package carmo.augusto.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import carmo.augusto.core.repositories.ISeasonRepository
import carmo.augusto.core.repositories.Result
import carmo.augusto.core.utils.SingleEventWrapper
import carmo.augusto.core.viewmodels.AbsSeasonEpisodesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class SeasonEpisodesViewModel(
    private val seasonRepository: ISeasonRepository,
) : AbsSeasonEpisodesViewModel() {

    private val _pageState = MutableLiveData<PageState>(PageState.Idle)
    override val pageState: LiveData<PageState>
        get() = _pageState

    private val _navRequest = MutableLiveData<SingleEventWrapper<NavigationRequest>>()
    override val navRequest: LiveData<SingleEventWrapper<NavigationRequest>>
        get() = _navRequest

    private val _message = MutableLiveData<SingleEventWrapper<Message>>()
    override val message: LiveData<SingleEventWrapper<Message>>
        get() = _message

    private val pageStateMutex = Mutex()

    override fun load(seasonId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            pageStateMutex.withLock {
                val pageState = pageState.value
                if (pageState is PageState.Loading || pageState is PageState.Loaded) {
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    _pageState.value = PageState.Loading
                }
            }

            val seasonEpisodesResult = seasonRepository.fetchSeasonEpisodes(seasonId = seasonId)

            pageStateMutex.withLock {
                if (pageState.value !is PageState.Loading) {
                    return@launch
                }

                when (seasonEpisodesResult) {
                    is Result.Success -> {
                        _pageState.postValue(PageState.Loaded(seasonEpisodesResult.data))
                    }
                    is Result.Error.Unknown,
                    is Result.Error.NetworkRequest,
                    -> {
                        _pageState.postValue(PageState.Error.Generic)
                        _message.postValue(SingleEventWrapper(Message.LoadingError.Generic))
                    }
                    is Result.Error.NoInternet -> {
                        _pageState.postValue(PageState.Error.NoInternet)
                        _message.postValue(SingleEventWrapper(Message.LoadingError.NoInternet))
                    }
                    is Result.Error.Server -> {
                        _pageState.postValue(PageState.Error.Server)
                        _message.postValue(SingleEventWrapper(Message.LoadingError.Server))
                    }
                }
            }
        }
    }

    override fun onEpisodeClicked(episodeId: Int) {
        _navRequest.postValue(
            SingleEventWrapper(NavigationRequest.Episode(episodeId = episodeId))
        )
    }
}