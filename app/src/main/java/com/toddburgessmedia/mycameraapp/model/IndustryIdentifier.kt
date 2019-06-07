package com.toddburgessmedia.mycameraapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IndustryIdentifier (

    val type : String?,
    val identifier: String?
) : Parcelable