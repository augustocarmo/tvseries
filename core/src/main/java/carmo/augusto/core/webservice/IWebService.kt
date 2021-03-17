package carmo.augusto.core.webservice

import carmo.augusto.core.entities.Episode
import carmo.augusto.core.entities.Series

interface IWebService {

    fun fetchSeries(seriesId: Int): Result<out Series>

    fun fetchSeriesList(page: Int): Result<out List<Series>>

    fun fetchSeriesByName(name: String): Result<out List<Series>>

    fun fetchSeasonEpisodes(seasonId: Int): Result<out List<Episode>>

    fun fetchEpisode(episodeId: Int): Result<out Episode>
}