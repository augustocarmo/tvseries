package carmo.augusto.webservice.entities

import com.google.gson.annotations.SerializedName
import carmo.augusto.core.entities.Season as CoreSeason

class Season(
    @SerializedName(value = "id")
    override val id: Int,
    @SerializedName(value = "number")
    override val number: Int
) : CoreSeason()