package carmo.augusto.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import carmo.augusto.core.repositories.ISeriesRepository
import carmo.augusto.core.repositories.Result
import carmo.augusto.core.utils.SingleEventWrapper
import carmo.augusto.core.viewmodels.AbsSeriesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SeriesViewModel(private val seriesRepository: ISeriesRepository) : AbsSeriesViewModel() {

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

    override fun load(seriesId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            pageStateMutex.withLock {
                val pageState = pageState.value
                if (pageState is PageState.Loading || pageState is PageState.Loaded) {
                    return@launch
                }

                _pageState.postValue(PageState.Loading)
            }

            val seriesResult = seriesRepository.fetchSeries(seriesId = seriesId)

            pageStateMutex.withLock {
                if (pageState.value !is PageState.Loading) {
                    return@launch
                }

                when (seriesResult) {
                    is Result.Success -> {
                        _pageState.postValue(PageState.Loaded(seriesResult.data))
                    }
                    is Result.Error.Unknown,
                    is Result.Error.NetworkRequest -> {
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