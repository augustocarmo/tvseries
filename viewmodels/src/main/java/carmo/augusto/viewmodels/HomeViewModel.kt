package carmo.augusto.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import carmo.augusto.core.entities.Series
import carmo.augusto.core.repositories.ISeriesRepository
import carmo.augusto.core.repositories.Result
import carmo.augusto.core.utils.SingleEventWrapper
import carmo.augusto.core.viewmodels.AbsHomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class HomeViewModel(private val seriesRepository: ISeriesRepository) : AbsHomeViewModel() {

    private val _pageState = MutableLiveData<PageState>()
    override val pageState: LiveData<PageState>
        get() = _pageState

    private val _navRequest = MutableLiveData<SingleEventWrapper<NavigationRequest>>()
    override val navRequest: LiveData<SingleEventWrapper<NavigationRequest>>
        get() = _navRequest

    private val _message = MutableLiveData<SingleEventWrapper<Message>>()
    override val message: LiveData<SingleEventWrapper<Message>>
        get() = _message

    private val pageStateMutex = Mutex()

    override fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            pageStateMutex.withLock {
                val pageState = pageState.value
                if (pageState == PageState.Loading || pageState is PageState.Loaded) {
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    _pageState.value = PageState.Loading
                }
            }

            val seriesListResult = seriesRepository.fetchSeriesList(page = FIRST_PAGE)

            pageStateMutex.withLock {
                if (pageState.value !is PageState.Loading) {
                    return@launch
                }

                when (seriesListResult) {
                    is Result.Success -> {
                        _pageState.postValue(
                            PageState.Loaded.Loaded(
                                series = seriesListResult.data,
                                canLoadMore = true
                            )
                        )
                    }
                    is Result.Error.NoInternet -> {
                        _pageState.postValue(PageState.Error.NoInternet)

                        _message.postValue(SingleEventWrapper(Message.LoadingError.NoInternet))
                    }
                    is Result.Error.Server -> {
                        _pageState.postValue(PageState.Error.Server)

                        _message.postValue(SingleEventWrapper(Message.LoadingError.Server))
                    }
                    is Result.Error.NetworkRequest -> {
                        val canLoadMore = seriesListResult.code == CANT_LOAD_MORE_ERROR_CODE

                        _pageState.postValue(PageState.Error.Generic)

                        if (canLoadMore) {
                            _message.postValue(SingleEventWrapper(Message.LoadingError.Generic))
                        } else {
                            _message.postValue(
                                SingleEventWrapper(Message.LoadingError.AllSeriesAlreadyLoaded)
                            )
                        }
                    }
                    is Result.Error.Unknown -> {
                        _pageState.postValue(PageState.Error.Generic)

                        _message.postValue(SingleEventWrapper(Message.LoadingError.Generic))
                    }
                }
            }
        }
    }

    override fun onLoadMoreButtonClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSeriesList: ArrayList<Series>
            pageStateMutex.withLock {
                val pageState = pageState.value
                if (pageState !is PageState.Loaded.Loaded) {
                    return@launch
                }

                currentSeriesList = ArrayList(pageState.series)

                withContext(Dispatchers.Main) {
                    _pageState.value = PageState.Loaded.LoadingMore(series = currentSeriesList)
                }
            }

            val maxSeriesId = currentSeriesList.maxOf {
                it.id
            }

            val pageToLoad = (maxSeriesId / SERIES_PER_PAGE) + 1

            val seriesListResult = seriesRepository.fetchSeriesList(page = pageToLoad)

            pageStateMutex.withLock {
                if (pageState.value !is PageState.Loaded.LoadingMore) {
                    return@launch
                }

                when (seriesListResult) {
                    is Result.Success -> {
                        _pageState.postValue(
                            PageState.Loaded.Loaded(
                                series = currentSeriesList + seriesListResult.data,
                                canLoadMore = true
                            )
                        )
                    }
                    is Result.Error.NoInternet -> {
                        _pageState.postValue(
                            PageState.Loaded.Loaded(series = currentSeriesList, canLoadMore = true)
                        )

                        _message.postValue(SingleEventWrapper(Message.LoadingError.NoInternet))
                    }
                    is Result.Error.Server -> {
                        _pageState.postValue(
                            PageState.Loaded.Loaded(series = currentSeriesList, canLoadMore = true)
                        )

                        _message.postValue(SingleEventWrapper(Message.LoadingError.Server))
                    }
                    is Result.Error.NetworkRequest -> {
                        val canLoadMore = seriesListResult.code == CANT_LOAD_MORE_ERROR_CODE

                        _pageState.postValue(
                            PageState.Loaded.Loaded(
                                series = currentSeriesList,
                                canLoadMore = canLoadMore
                            )
                        )

                        if (canLoadMore) {
                            _message.postValue(SingleEventWrapper(Message.LoadingError.Generic))
                        } else {
                            _message.postValue(
                                SingleEventWrapper(Message.LoadingError.AllSeriesAlreadyLoaded)
                            )
                        }
                    }
                    is Result.Error.Unknown -> {
                        _pageState.postValue(
                            PageState.Loaded.Loaded(series = currentSeriesList, canLoadMore = true)
                        )
                    }
                }
            }
        }
    }

    override fun onSeriesClicked(series: Series) {
        _navRequest.postValue(
            SingleEventWrapper(NavigationRequest.Series(series = series))
        )
    }

    override fun onSearchButtonCLicked() {
        _navRequest.postValue(SingleEventWrapper(NavigationRequest.SearchSeries))
    }

    companion object {
        private const val FIRST_PAGE = 0
        private const val SERIES_PER_PAGE = 250
        private const val CANT_LOAD_MORE_ERROR_CODE = 404
    }
}