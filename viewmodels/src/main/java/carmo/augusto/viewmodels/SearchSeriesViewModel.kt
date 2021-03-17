package carmo.augusto.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import carmo.augusto.core.entities.Series
import carmo.augusto.core.repositories.ISeriesRepository
import carmo.augusto.core.utils.SingleEventWrapper
import carmo.augusto.core.viewmodels.AbsSearchSeriesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import carmo.augusto.core.repositories.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SearchSeriesViewModel(
    private val seriesRepository: ISeriesRepository,
) : AbsSearchSeriesViewModel() {

    private val _pageState = MutableLiveData<PageState>(PageState.Idle)
    override val pageState: LiveData<PageState>
        get() = _pageState

    private val _navRequest = MutableLiveData<SingleEventWrapper<NavigationRequest>>()
    override val navRequest: LiveData<SingleEventWrapper<NavigationRequest>>
        get() = _navRequest

    private val _message = MutableLiveData<SingleEventWrapper<Message>>()
    override val message: LiveData<SingleEventWrapper<Message>>
        get() = _message

    private var currentSearchJob: Job? = null

    private val pageStateMutex = Mutex()

    private var lastQuery = ""

    override fun search(query: String) {
        currentSearchJob?.cancel()

        currentSearchJob = viewModelScope.launch(Dispatchers.IO) {
            _pageState.postValue(PageState.Searching(query = query))

            if (query.isBlank()) {
                _pageState.postValue(PageState.Loaded(seriesList = emptyList()))

                return@launch
            }

            val seriesResult = seriesRepository.searchSeries(name = query)

            pageStateMutex.withLock {
                if (pageState.value !is PageState.Searching) {
                    return@launch
                }

                when (seriesResult) {
                    is Result.Success -> {
                        _pageState.postValue(PageState.Loaded(seriesResult.data))
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

    override fun redoLastSearch() {
        search(query = lastQuery)
    }

    override fun onSeriesClicked(series: Series) {
        _navRequest.postValue(SingleEventWrapper(NavigationRequest.Series(series = series)))
    }
}