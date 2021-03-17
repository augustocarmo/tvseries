package carmo.augusto.core.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import carmo.augusto.core.entities.Episode
import carmo.augusto.core.entities.Series
import carmo.augusto.core.utils.SingleEventWrapper

abstract class AbsSeasonEpisodesViewModel : ViewModel() {

    abstract val pageState: LiveData<PageState>
    abstract val navRequest: LiveData<SingleEventWrapper<NavigationRequest>>
    abstract val message: LiveData<SingleEventWrapper<Message>>

    abstract fun load(seasonId: Int)

    abstract fun onEpisodeClicked(episodeId: Int)

    sealed class PageState {

        object Idle: PageState()
        object Loading: PageState()
        class Loaded(val episodes: List<Episode>) : PageState()
        sealed class Error : PageState() {

            object NoInternet : Error()
            object Server : Error()
            object Generic : Error()
        }
    }

    sealed class NavigationRequest {

        class Episode(val episodeId: Int): NavigationRequest()
    }

    sealed class Message {

        sealed class LoadingError : Message() {

            object NoInternet : LoadingError()
            object Server : LoadingError()
            object Generic : LoadingError()
        }
    }
}