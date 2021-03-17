package carmo.augusto.core.extensions

import carmo.augusto.core.webservice.Result
import carmo.augusto.core.repositories.Result as RepositoryResult

fun <T> Result<T>.convertToRepositoryResult(): RepositoryResult<T> {
    return when (this) {
        is Result.Error.NoInternet -> {
            RepositoryResult.Error.NoInternet(exception = this.exception)
        }
        is Result.Error.Request -> {
            RepositoryResult.Error.NetworkRequest(code = this.code, message = this.message)
        }
        is Result.Error.Server -> {
            RepositoryResult.Error.Server(code = this.code, message = this.message)
        }
        is Result.Error.Unknown -> {
            RepositoryResult.Error.Unknown(exception = this.exception)
        }
        is Result.Success -> {
            RepositoryResult.Success(data = this.data)
        }
    }
}