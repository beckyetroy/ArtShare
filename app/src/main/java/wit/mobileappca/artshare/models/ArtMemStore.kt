package wit.mobileappca.artshare.models

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class ArtMemStore : ArtStore, AnkoLogger {

    val arts = ArrayList<ArtModel>()

    override fun findAll(): List<ArtModel> {
        return arts
    }

    override fun create(art: ArtModel) {
        art.id = getId()
        arts.add(art)
        logAll()
    }

    override fun search(searchTerm: String): List<ArtModel> {
        return arts.filter { art -> art.title.toLowerCase().contains(searchTerm.toLowerCase()) }
    }

    override fun update(art: ArtModel) {
        var foundArt: ArtModel? = arts.find { m -> m.id == art.id }
        if (foundArt != null) {
            foundArt.title = art.title
            foundArt.image = art.image
            foundArt.type = art.type
            foundArt.description = art.description
            foundArt.image = art.image
            foundArt.date = art.date
            foundArt.lat = art.lat
            foundArt.lng = art.lng
            foundArt.zoom = art.zoom
            logAll();
        }
    }

    override fun delete(art: ArtModel) {
        arts.remove(art)
    }

    fun logAll() {
        arts.forEach { info("${it}") }
    }
}