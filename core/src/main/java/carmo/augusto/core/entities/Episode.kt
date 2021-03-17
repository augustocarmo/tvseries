package carmo.augusto.core.entities

abstract class Episode {

    abstract val id: Int
    abstract val name: String
    abstract val number: Int
    abstract val season: Int
    abstract val summary: String?
    abstract val image: Image?
}