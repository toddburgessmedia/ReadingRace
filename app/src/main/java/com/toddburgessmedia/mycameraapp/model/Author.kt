package com.toddburgessmedia.mycameraapp.model

import com.squareup.moshi.Json

class Author (

    val url : String,
    @Json(name = "name") val author : String
)
