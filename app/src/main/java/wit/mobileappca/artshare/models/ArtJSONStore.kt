package org.wit.artshare.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.AnkoLogger
import org.wit.artshare.helpers.exists
import org.wit.artshare.helpers.read
import org.wit.artshare.helpers.write
import java.util.*

val JSON_FILE = "arts.json"
val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<ArrayList<ArtModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class ArtJSONStore : ArtStore, AnkoLogger {

    val context: Context
    var arts = mutableListOf<ArtModel>()

    constructor (context: Context) {
        this.context = context
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<ArtModel> {
        return arts
    }

    override fun create(art: ArtModel) {
        art.id = generateRandomId()
        arts.add(art)
        serialize()
    }

    override fun search(searchTerm: String): List<ArtModel> {
        return arts.filter { art -> art.title.toLowerCase().contains(searchTerm.toLowerCase()) }
    }

    override fun update(art: ArtModel) {
        val artsList = findAll() as ArrayList<ArtModel>
        var foundArt: ArtModel? = artsList.find { m -> m.id == art.id }
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
        }
        serialize()
    }

    override fun delete(art: ArtModel) {
        arts.remove(art)
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(arts, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        arts = Gson().fromJson(jsonString, listType)
    }
}