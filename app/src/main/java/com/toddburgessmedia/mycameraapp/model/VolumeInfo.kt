package com.toddburgessmedia.mycameraapp.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class VolumeInfo (

    val title : String? = null,
    val subtitle : String? = null,
    val publishedDate : String? = null,
    val authors : List<String>?,
    val description : String? = null,
    val pageCount : Int? = 0,
    val categories : List<String>?,
    val imageLinks : ImageLink?

) : Parcelable