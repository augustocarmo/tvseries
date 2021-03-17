package carmo.augusto.core.entities

abstract class Season {

    abstract val id: Int
    abstract val number: Int

    var episodes: List<Episode>? = null
}