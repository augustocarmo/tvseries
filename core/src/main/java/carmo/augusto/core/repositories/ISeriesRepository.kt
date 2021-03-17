package carmo.augusto.core.repositories

import carmo.augusto.core.entities.Series

interface ISeriesRepository : IRepository {

    fun fetchSeries(seriesId: Int): Result<out Series>

    fun fetchSeriesList(page: Int): Result<out List<Series>>

    fun searchSeries(name: String): Result<out List<Series>>
}