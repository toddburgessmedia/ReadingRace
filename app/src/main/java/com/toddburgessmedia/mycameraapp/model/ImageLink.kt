package com.toddburgessmedia.mycameraapp.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ImageLink (

    val smallThumbnail : String? = null,
    val thumbnail : String? = null,
    val small : String? = null,
    val medium : String? = null,
    val large : String? = null,
    val extraLarge : String? = null

) : Parcelable