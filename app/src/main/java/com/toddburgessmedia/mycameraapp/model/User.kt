package com.toddburgessmedia.mycameraapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    val uid : String?,
    val email : String?,
    val name : String?,
    val booksRead : Int,
    val booksReading : List<String>? = null
) : Parcelable