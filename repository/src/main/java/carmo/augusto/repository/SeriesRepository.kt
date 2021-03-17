package carmo.augusto.repository

import carmo.augusto.core.entities.Series
import carmo.augusto.core.extensions.convertToRepositoryResult
import carmo.augusto.core.repositories.ISeriesRepository
import carmo.augusto.core.repositories.Result
import carmo.augusto.core.webservice.IWebService

class SeriesRepository(private val webService: IWebService) : ISeriesRepository {

    override fun fetchSeries(seriesId: Int): Result<out Series> {
        val webServiceResult = webService.fetchSeries(seriesId = seriesId)

        return webServiceResult.convertToRepositoryResult()
    }

    override fun fetchSeriesList(page: Int): Result<out List<Series>> {
        val webServiceResult = webService.fetchSeriesList(page = page)

        return webServiceResult.convertToRepositoryResult()
    }

    override fun searchSeries(name: String): Result<out List<Series>> {
        val webServiceResult = webService.fetchSeriesByName(name = name)

        return webServiceResult.convertToRepositoryResult()
    }
}