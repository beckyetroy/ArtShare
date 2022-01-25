package org.wit.artshare.models

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class ArtMemStore : ArtStore, AnkoLogger {

    val movies = ArrayList<ArtModel>()

    override fun findAll(): List<ArtModel> {
        return movies
    }

    override fun create(art: ArtModel) {
        art.id = getId()
        movies.add(art)
        logAll()
    }

    override fun search(searchTerm: String): List<ArtModel> {
        return movies.filter { movie -> movie.title.toLowerCase().contains(searchTerm.toLowerCase()) }
    }

    override fun update(art: ArtModel) {
        var foundArt: ArtModel? = movies.find { m -> m.id == art.id }
        if (foundArt != null) {
            foundArt.title = art.title
            foundArt.year = art.year
            foundArt.director = art.director
            foundArt.description = art.description
            foundArt.image = art.image
            foundArt.rating = art.rating
            foundArt.lat = art.lat
            foundArt.lng = art.lng
            foundArt.zoom = art.zoom
            logAll();
        }
    }

    override fun delete(art: ArtModel) {
        movies.remove(art)
    }

    fun logAll() {
        movies.forEach { info("${it}") }
    }
}