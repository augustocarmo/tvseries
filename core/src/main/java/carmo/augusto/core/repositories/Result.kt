package carmo.augusto.core.repositories

sealed class Result<T> {

    class Success<T>(val data: T) : Result<T>()
    sealed class Error<T> : Result<T>() {
        class NoInternet<T>(val exception: Exception? = null) : Error<T>()
        class NetworkRequest<T>(val code: Int, val message: String) : Error<T>()
        class Server<T>(val code: Int, val message: String) : Error<T>()
        class Unknown<T>(val exception: Exception? = null) : Error<T>()
    }
}