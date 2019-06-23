package com.toddburgessmedia.mycameraapp.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Item (

    var id : String? = null,
    var volumeInfo : VolumeInfo? = null

) : Parcelable