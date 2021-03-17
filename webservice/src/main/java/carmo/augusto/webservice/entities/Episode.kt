package carmo.augusto.webservice.entities

import com.google.gson.annotations.SerializedName
import carmo.augusto.core.entities.Episode as CoreEpisode

data class Episode(
    @SerializedName(value = "id")
    override val id: Int,
    @SerializedName(value = "name")
    override val name: String,
    @SerializedName(value = "number")
    override val number: Int,
    @SerializedName(value = "season")
    override val season: Int,
    @SerializedName(value = "summary")
    override val summary: String?,
    @SerializedName(value = "image")
    override val image: Image?
) : CoreEpisode()