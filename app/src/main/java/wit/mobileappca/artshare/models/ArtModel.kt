package org.wit.artshare.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ArtModel(var id: Long = 0,
                    var title: String = "",
                    var year: Int = 0,
                    var director: String = "",
                    var description: String = "",
                    var image: String = "",
                    var rating: Float = 0.0f,
                    var lat : Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable

//For the purposes of the default location marker separate from movie, set when the GMapsActivity is called
@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable