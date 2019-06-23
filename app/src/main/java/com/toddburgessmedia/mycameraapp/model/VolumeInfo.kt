package com.toddburgessmedia.mycameraapp.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class VolumeInfo (

    var title : String? = null,
    var subtitle : String? = null,
    var publishedDate : String? = null,
    var authors : List<String>? = null,
    var description : String? = null,
    var pageCount : Int? = 0,
    var categories : List<String>? = null,
    var imageLinks : ImageLink? = null,
    var industryIdentifiers : List<IndustryIdentifier>? = null

) : Parcelable