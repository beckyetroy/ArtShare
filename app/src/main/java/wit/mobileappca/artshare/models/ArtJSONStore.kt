package wit.mobileappca.artshare.models

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.AnkoLogger
import wit.mobileappca.artshare.helpers.exists
import wit.mobileappca.artshare.helpers.read
import wit.mobileappca.artshare.helpers.write
import java.util.*

val JSON_FILE = "arts.json"
val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<ArrayList<ArtModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

//No longer applicable with FB storage, commented out old code

class ArtJSONStore : ArtStore, AnkoLogger {

    val context: Context
    var arts = mutableListOf<ArtModel>()

    constructor (context: Context) {
        this.context = context
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    /*
    override fun findAll(): MutableList<ArtModel> {
        return arts
    }
     */

    override fun findAll(artsList: MutableLiveData<List<ArtModel>>) {
        TODO("Not yet implemented")
    }

    override fun findAll(email: String, artsList: MutableLiveData<List<ArtModel>>) {
        TODO("Not yet implemented")
    }

    override fun findById(email: String, id: String, art: MutableLiveData<ArtModel>) {
        TODO("Not yet implemented")
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, art: ArtModel) {
        TODO("Not yet implemented")
    }

    //override fun create(art: ArtModel) {
    //    art.uid = generateRandomId().toString()
    //    arts.add(art)
    //    serialize()
    //}

    override fun search(searchTerm: String): List<ArtModel> {
        return arts.filter { art -> art.title.toLowerCase().contains(searchTerm.toLowerCase()) }
    }

    override fun delete(userid: String, artid: String) {
        TODO("Not yet implemented")
    }

    override fun update(userid: String, artid: String, art: ArtModel) {
        TODO("Not yet implemented")
    }

    /*
    override fun update(email: String, art: ArtModel) {
        val artsList = findAll() as ArrayList<ArtModel>
        var foundArt: ArtModel? = artsList.find { m -> m.uid == art.uid }
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
            foundArt.email = art.email
        }
        serialize()
    }

    override fun delete(email: String, art: ArtModel) {
        arts.remove(art)
        serialize()
    }

     */

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(arts, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        arts = Gson().fromJson(jsonString, listType)
    }
}