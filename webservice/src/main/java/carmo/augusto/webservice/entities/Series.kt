package carmo.augusto.webservice.entities

import com.google.gson.annotations.SerializedName
import carmo.augusto.core.entities.Series as CoreSeries

class Series(
    @SerializedName(value = "id")
    override val id: Int,
    @SerializedName(value = "name")
    override val name: String,
    @SerializedName(value = "image")
    override val posterImage: Image?,
    @SerializedName(value = "schedule")
    override val schedule: Schedule,
    @SerializedName(value = "genres")
    override val genres: List<String>,
    @SerializedName(value = "summary")
    override val summary: String?,
    @SerializedName("_embedded")
    private val embedded: Embedded
) : CoreSeries() {

    override val seasons get() = embedded.seasons
}

class Embedded(
    @SerializedName("seasons")
    val seasons: List<Season>
)