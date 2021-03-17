package carmo.augusto.core.utils

class SingleEventWrapper<T>(private val data: T) {

    var hasBeenHandled: Boolean = false
        private set

    @Synchronized
    fun getDataIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true

            data
        }
    }

    fun peekData() = data
}