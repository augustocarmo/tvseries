package carmo.augusto.core.repositories

import carmo.augusto.core.entities.Episode

interface IEpisodeRepository : IRepository {

    fun fetchEpisode(episodeId: Int): Result<out Episode>
}