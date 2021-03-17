package carmo.augusto.webservice.entities

import com.google.gson.annotations.SerializedName

class SeriesFromSearch(
    @SerializedName(value = "show")
    val series: Series
)