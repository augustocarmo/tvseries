package carmo.augusto.core.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import carmo.augusto.core.entities.Series as CoreSeries
import carmo.augusto.core.utils.SingleEventWrapper

abstract class AbsSearchSeriesViewModel : ViewModel() {

    abstract val pageState: LiveData<PageState>
    abstract val navRequest: LiveData<SingleEventWrapper<NavigationRequest>>
    abstract val message: LiveData<SingleEventWrapper<Message>>

    abstract fun search(query: String)

    abstract fun redoLastSearch()

    abstract fun onSeriesClicked(series: CoreSeries)

    sealed class PageState {

        object Idle: PageState()
        class Searching(val query: String): PageState()
        class Loaded(val seriesList: List<CoreSeries>) : PageState()
        sealed class Error : PageState() {

            object NoInternet : Error()
            object Server : Error()
            object Generic : Error()
        }
    }

    sealed class NavigationRequest {

        class Series(val series: CoreSeries): NavigationRequest()
    }

    sealed class Message {

        sealed class LoadingError : Message() {

            object NoInternet : LoadingError()
            object Server : LoadingError()
            object Generic : LoadingError()
        }
    }
}