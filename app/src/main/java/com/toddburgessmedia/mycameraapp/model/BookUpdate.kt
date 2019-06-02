package com.toddburgessmedia.mycameraapp.model

sealed class BookUpdate {
    data class ReadingUpdate(val libraryList: List<Book>) : BookUpdate()
    data class NewBook(var book: Book?) : BookUpdate()
    object NewUser : BookUpdate()
}

