package carmo.augusto.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import carmo.augusto.core.repositories.Result
import carmo.augusto.core.repositories.IEpisodeRepository
import carmo.augusto.core.utils.SingleEventWrapper
import carmo.augusto.core.viewmodels.AbsEpisodeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class EpisodeViewModel(private val episodeRepository: IEpisodeRepository) : AbsEpisodeViewModel() {

    private val _pageState = MutableLiveData<PageState>(PageState.Idle)
    override val pageState: LiveData<PageState>
        get() = _pageState

    private val _message = MutableLiveData<SingleEventWrapper<Message>>()
    override val message: LiveData<SingleEventWrapper<Message>>
        get() = _message

    private val pageStateMutex = Mutex()

    override fun loadEpisode(episodeId: Int) {
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

            val episodeResult = episodeRepository.fetchEpisode(episodeId = episodeId)

            pageStateMutex.withLock {
                if (pageState.value !is PageState.Loading) {
                    return@launch
                }

                when (episodeResult) {
                    is Result.Success -> {
                        _pageState.postValue(PageState.Loaded(episodeResult.data))
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
}