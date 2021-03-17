package carmo.augusto.webservice

import carmo.augusto.core.webservice.IWebService
import carmo.augusto.core.webservice.Result
import carmo.augusto.webservice.entities.Episode
import carmo.augusto.webservice.entities.Series
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

class WebService(private val api: Api) : IWebService {

    override fun fetchSeries(seriesId: Int): Result<out Series> {
        val call = api.fetchSeries(seriesId = seriesId)

        return request(call)
    }

    override fun fetchSeriesList(page: Int): Result<List<Series>> {
        val call = api.fetchSeriesList(page = page)

        return request(call)
    }

    override fun fetchSeriesByName(name: String): Result<List<Series>> {
        val call = api.fetchSeriesByName(name = name)

        return request(call).map { seriesFromSearchList ->
            seriesFromSearchList.map { seriesFromSearch ->
                seriesFromSearch.series
            }
        }
    }

    override fun fetchSeasonEpisodes(
        seasonId: Int,
    ): Result<out List<Episode>> {
        val call = api.fetchSeasonEpisodes(seasonId = seasonId)

        return request(call)
    }

    override fun fetchEpisode(episodeId: Int): Result<Episode> {
        val call = api.fetchEpisode(episodeId = episodeId)

        return request(call)
    }

    private fun <T> parseResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            Result.Success(response.body())
        } else {
            when (response.code()) {
                in 400..499 -> Result.Error.Request(
                    code = response.code(),
                    message = response.message()
                )
                in 500..599 -> Result.Error.Server(
                    code = response.code(),
                    message = response.message()
                )
                else -> Result.Error.Unknown()
            }
        }
    }

    private fun <T> request(call: Call<T>): Result<T> {
        return try {
            parseResponse(response = call.execute())
        } catch (ioException: IOException) {
            Result.Error.NoInternet(exception = ioException)
        } catch (exception: Exception) {
            Result.Error.Unknown(exception = exception)
        }
    }

    private fun <T, U> Result<T>.map(map: (T) -> U): Result<U> {
        return when (this) {
            is Result.Error.NoInternet -> {
                Result.Error.NoInternet(exception = this.exception)
            }
            is Result.Error.Request -> {
                Result.Error.Request(code = this.code, message = this.message)
            }
            is Result.Error.Server -> {
                Result.Error.Server(code = this.code, message = this.message)
            }
            is Result.Error.Unknown -> {
                Result.Error.Unknown(exception = this.exception)
            }
            is Result.Success -> {
                Result.Success(data = map(this.data))
            }
        }
    }
}