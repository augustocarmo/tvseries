package carmo.augusto.core.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import carmo.augusto.core.entities.Series as CoreSeries
import carmo.augusto.core.utils.SingleEventWrapper

abstract class AbsHomeViewModel : ViewModel() {

    abstract val pageState: LiveData<PageState>
    abstract val navRequest: LiveData<SingleEventWrapper<NavigationRequest>>
    abstract val message: LiveData<SingleEventWrapper<Message>>

    abstract fun load()

    abstract fun onLoadMoreButtonClicked()

    abstract fun onSeriesClicked(series: CoreSeries)

    abstract fun onSearchButtonCLicked()

    sealed class PageState {

        object Idle : PageState()
        object Loading : PageState()
        sealed class Loaded(val series: List<CoreSeries>) : PageState() {

            class Loaded(
                series: List<CoreSeries>,
                val canLoadMore: Boolean,
            ) : PageState.Loaded(series = series)
            class LoadingMore(series: List<CoreSeries>) : PageState.Loaded(series = series)
        }

        sealed class Error : PageState() {

            object NoInternet : Error()
            object Server : Error()
            object Generic : Error()
        }
    }

    sealed class NavigationRequest {

        class Series(val series: CoreSeries) : NavigationRequest()
        object SearchSeries : NavigationRequest()
    }

    sealed class Message {

        sealed class LoadingError : Message() {

            object NoInternet : LoadingError()
            object Server : LoadingError()
            object Generic : LoadingError()
            object AllSeriesAlreadyLoaded : LoadingError()
        }
    }
}