package com.toddburgessmedia.mycameraapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.toddburgessmedia.mycameraapp.firebase.FireStoreModel
import com.toddburgessmedia.mycameraapp.model.*
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CameraViewModel(application: Application, val firestore : FireStoreModel) : AndroidViewModel(application), CoroutineScope {

    init {
        firestore.viewModel = this
    }

    val cameraObserver = MutableLiveData<CameraAction>()

    val bookUpdateObserver = MutableLiveData<BookUpdate>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()

    lateinit var user: User

    override fun onCleared() {
        super.onCleared()

        coroutineContext.cancelChildren()
    }

    fun getBookInfo(isbn: String): Maybe<Book> {

        val bookFactory = BookFactory.makeRetrofitService()

        return Maybe.create { emitter ->
            launch {
                val request = bookFactory.getBookInfo(isbn).await()
                val book = request
                val bookUpdate = NewBook(book)
                if (book.items.isNotEmpty()) {
                    emitter.onSuccess(book)
                } else {
                    emitter.onError(Throwable("No Book found"))
                }
            }
        }
    }

    fun addBookFromBitmap(bitMap: Bitmap) {

        val vision = ReadingRaceVision()

        val result = vision.getBarCode(bitMap)
            .flatMap { isbn ->
                getBookInfo(isbn)
            }
            .flatMap {book ->
                firestore.writeBookForUser(book)
            }
            .flatMapCompletable {
                firestore.addBooktoUser(it)
            }
            .andThen(firestore.getAllBooksReadingRx())
            .subscribe{ booksReading ->
                Log.d("mycamera", "Rx version added book to user ${booksReading.toString()}")
                bookUpdateObserver.postValue(ReadingUpdate(booksReading))
            }

    }

    fun firebaseConversion(isbn : String) {

        val firebaseAnalytics = FirebaseAnalytics.getInstance(getApplication())

        launch {
            firebaseAnalytics.logEvent("scan_book",null)
        }
    }

    fun userExists(uid : String?) {

        if (uid != null) {
            val result = firestore.userExists(uid)
                .flatMap { result ->
                    getNextStepRx(result)
                }
                .subscribe { nextStep ->
                    when (nextStep) {
                        is RegisterUser -> bookUpdateObserver.postValue(RegisterUser)
                        is ReadingUpdate -> firestore.getUserInfoFromUID(uid)
                    }
                }

        }

    }

    private fun getNextStepRx(result: Boolean) : Single<BookUpdate>{

        if (result) {
            return Single.just(ReadingUpdate(emptyList()))
        } else {
            return Single.just(RegisterUser)
        }

    }


    fun getNextLoginStep(next : BookUpdate) {

        when (next) {
            is RegisterUser -> {bookUpdateObserver.postValue(RegisterUser)}
            is NewUser -> {bookUpdateObserver.postValue(NewUser)}
            is ReadingUpdate -> {bookUpdateObserver.postValue(next)}
        }

    }


    fun createUser(user: User) {

        val result = firestore.createUser(user)
            .doOnError {
                Log.d("mycamera",it.localizedMessage)
            }
            .subscribe {
                bookUpdateObserver.postValue(NewUser)
            }

    }


    fun takePicture() {
        cameraObserver.postValue(CameraStart)
    }
}