package carmo.augusto.repository

import carmo.augusto.core.entities.Episode
import carmo.augusto.core.extensions.convertToRepositoryResult
import carmo.augusto.core.repositories.ISeasonRepository
import carmo.augusto.core.repositories.Result
import carmo.augusto.core.webservice.IWebService

class SeasonRepository(
    private val webService: IWebService
) : ISeasonRepository {

    override fun fetchSeasonEpisodes(seasonId: Int): Result<out List<Episode>> {
        val webServiceResult = webService.fetchSeasonEpisodes(seasonId = seasonId)

        return webServiceResult.convertToRepositoryResult()
    }
}