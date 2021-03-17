package carmo.augusto.webservice

import carmo.augusto.webservice.entities.Episode
import carmo.augusto.webservice.entities.Series
import carmo.augusto.webservice.entities.SeriesFromSearch
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET(value = "shows/{seriesId}?embed=seasons")
    fun fetchSeries(
        @Path(value = "seriesId") seriesId: Int
    ): Call<Series>

    @GET(value = "shows")
    fun fetchSeriesList(
        @Query(value = "page") page: Int
    ): Call<List<Series>>

    @GET(value = "search/shows")
    fun fetchSeriesByName(
        @Query(value = "q") name: String
    ): Call<List<SeriesFromSearch>>

    @GET("seasons/{seasonId}/episodes")
    fun fetchSeasonEpisodes(
        @Path(value = "seasonId") seasonId: Int
    ): Call<List<Episode>>

    @GET(value = "episodes/{episodeId}")
    fun fetchEpisode(
        @Path(value = "episodeId") episodeId: Int
    ): Call<Episode>
}