package com.toddburgessmedia.mycameraapp.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ImageLink (

    val smallThumbnail : String? = null,
    val thumbnail : String? = null

) : Parcelable