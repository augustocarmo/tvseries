package carmo.augusto.webservice.entities

import carmo.augusto.core.entities.Schedule as CoreSchedule
import com.google.gson.annotations.SerializedName

data class Schedule(
    @SerializedName(value = "time")
    override val time: String,
    @SerializedName(value = "days")
    override val days: List<String>
) : CoreSchedule()