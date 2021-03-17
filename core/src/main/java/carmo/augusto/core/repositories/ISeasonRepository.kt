package carmo.augusto.core.repositories

import carmo.augusto.core.entities.Episode

interface ISeasonRepository : IRepository {

    fun fetchSeasonEpisodes(seasonId: Int): Result<out List<Episode>>
}