package org.wit.artshare.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.util.*

@Parcelize
data class ArtModel(
    var id: Long = 0,
    var title: String = "",
    var image: String = "",
    var type: String = "",
    var description: String = "",
    var date: Date = Calendar.getInstance().time,
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var zoom: Float = 0f) : Parcelable

//For the purposes of the default location marker separate from art, set when the GMapsActivity is called
@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable