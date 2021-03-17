package carmo.augusto.core.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import carmo.augusto.core.entities.Episode
import carmo.augusto.core.utils.SingleEventWrapper

abstract class AbsEpisodeViewModel : ViewModel() {

    abstract val pageState: LiveData<PageState>
    abstract val message: LiveData<SingleEventWrapper<Message>>

    abstract fun loadEpisode(episodeId: Int)

    sealed class PageState {

        object Idle: PageState()
        object Loading: PageState()
        class Loaded(val episode: Episode) : PageState()
        sealed class Error : PageState() {

            object NoInternet : Error()
            object Server : Error()
            object Generic : Error()
        }
    }

    sealed class Message {

        sealed class LoadingError : Message() {

            object NoInternet : LoadingError()
            object Server : LoadingError()
            object Generic : LoadingError()
        }
    }
}