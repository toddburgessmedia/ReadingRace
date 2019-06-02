package com.toddburgessmedia.mycameraapp.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Book (

    val totalItems : Int?,
    val items : List<Item>

) : Parcelable