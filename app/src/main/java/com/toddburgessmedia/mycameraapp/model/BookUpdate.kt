package com.toddburgessmedia.mycameraapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class BookUpdate : Parcelable

@Parcelize
data class ReadingUpdate(val libraryList: List<Book>) : BookUpdate()

@Parcelize
data class NewBook(var book: Book?) : BookUpdate()

@Parcelize
object NewUser : BookUpdate()

@Parcelize
object RegisterUser : BookUpdate()


