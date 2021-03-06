package wit.mobileappca.artshare.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class ArtModel(
    var uid: String? = "",
    var title: String = "",
    var image: String = "",
    var type: String = "",
    var typeIndex: Int = 0,
    var description: String = "",
    var date: Date = Calendar.getInstance().time,
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var zoom: Float = 0f,
    var likes: Int = 0,
    var email: String? = "beckyet19@gmail.com",
    var profilepic: String = "") : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "title" to title,
            "image" to image,
            "type" to type,
            "typeIndex" to typeIndex,
            "description" to description,
            "date" to date,
            "lat" to lat,
            "lng" to lng,
            "zoom" to zoom,
            "likes" to likes,
            "email" to email,
            "profilepic" to profilepic
        )
    }
}


//For the purposes of the default location marker separate from art, set when the GMapsActivity is called
@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable