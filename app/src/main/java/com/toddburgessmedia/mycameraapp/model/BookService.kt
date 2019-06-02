package com.toddburgessmedia.mycameraapp.model

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Query

interface BookService {

    @GET("https://www.googleapis.com/books/v1/volumes?maxResults=1&startIndex=0")
    fun getBookInfo(@Query("q") query : String) : Deferred<Book>

}