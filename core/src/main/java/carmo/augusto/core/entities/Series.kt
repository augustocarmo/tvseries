package carmo.augusto.core.entities

abstract class Series {

    abstract val id: Int
    abstract val name: String
    abstract val posterImage: Image?
    abstract val schedule: Schedule
    abstract val genres: List<String>
    abstract val summary: String?
    abstract val seasons: List<Season>?
}