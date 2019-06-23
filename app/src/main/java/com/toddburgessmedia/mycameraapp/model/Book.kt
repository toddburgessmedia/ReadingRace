package com.toddburgessmedia.mycameraapp.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Book (

    var totalItems : Int?,
    var items : List<Item> = emptyList()

) : Parcelable {
    constructor() : this(0, emptyList())
}