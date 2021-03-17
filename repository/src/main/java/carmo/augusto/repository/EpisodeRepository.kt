package carmo.augusto.repository

import carmo.augusto.core.entities.Episode
import carmo.augusto.core.extensions.convertToRepositoryResult
import carmo.augusto.core.repositories.IEpisodeRepository
import carmo.augusto.core.repositories.Result
import carmo.augusto.core.webservice.IWebService

class EpisodeRepository(private val webService: IWebService) : IEpisodeRepository {

    override fun fetchEpisode(episodeId: Int): Result<out Episode> {
        val webServiceResult = webService.fetchEpisode(episodeId = episodeId)

        return webServiceResult.convertToRepositoryResult()
    }
}