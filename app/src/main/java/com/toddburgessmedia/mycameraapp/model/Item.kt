package com.toddburgessmedia.mycameraapp.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Item (

    val id : String? = null,
    val volumeInfo : VolumeInfo?

) : Parcelable