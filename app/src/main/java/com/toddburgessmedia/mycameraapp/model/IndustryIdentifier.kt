package com.toddburgessmedia.mycameraapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IndustryIdentifier (

    var type : String? = null,
    var identifier: String? = null
) : Parcelable