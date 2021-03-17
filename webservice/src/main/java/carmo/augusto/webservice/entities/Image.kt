package carmo.augusto.webservice.entities

import com.google.gson.annotations.SerializedName
import carmo.augusto.core.entities.Image as CoreImage

data class Image(
    @SerializedName(value = "medium")
    override val mediumUrl: String?,
    @SerializedName(value = "original")
    override val originalUrl: String?
) : CoreImage()