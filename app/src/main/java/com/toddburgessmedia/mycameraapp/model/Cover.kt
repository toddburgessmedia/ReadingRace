package com.toddburgessmedia.mycameraapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Cover (

    val small : String?,
    val large : String?,
    val medium : String?
)