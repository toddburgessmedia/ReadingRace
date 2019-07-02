package com.toddburgessmedia.mycameraapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.toddburgessmedia.mycameraapp.firebase.FireStoreModel
import com.toddburgessmedia.mycameraapp.model.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CameraViewModel(application: Application, val firestore : FireStoreModel) : AndroidViewModel(application), CoroutineScope {

    init {
        firestore.viewModel = this
    }

    val cameraObserver = MutableLiveData<CameraAction>()

    val bookUpdateObserver = MutableLiveData<BookUpdate>()

    val disposables = CompositeDisposable()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()

    lateinit var user: User

    override fun onCleared() {
        super.onCleared()

        coroutineContext.cancelChildren()
        disposables.clear()
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
            .doOnError { Log.d ("mycamera","error occured ${it.localizedMessage}") }
            .flatMap { isbn ->
                getBookInfo(isbn)
            }
            .doOnError { Log.d ("mycamera","error occured ${it.localizedMessage}") }
            .flatMap {book ->
                firestore.writeBookForUser(book)
            }
            .flatMapCompletable { book ->
                firestore.addBooktoUser(book)
            }
            .andThen(firestore.getAllBooksReading())
            .subscribe({ booksReading ->
                Log.d("mycamera", "Rx version added book to user ${booksReading.toString()}")
                bookUpdateObserver.postValue(ReadingUpdate(booksReading))
            },{ error ->
                Log.d("mycamera","error occured adding book ${error.localizedMessage}")
                cameraObserver.postValue(CameraFail)
            })

        disposables.add(result)
    }

    fun firebaseConversion(isbn : String) : Completable {

        val firebaseAnalytics = FirebaseAnalytics.getInstance(getApplication())

        return Completable.create {emitter ->
            launch {
                firebaseAnalytics.logEvent("scan_book", null)
            }
            emitter.onComplete()
        }
    }

    fun checkUserExists(uid : String?) {

        if (uid != null) {
            val result = firestore.userExists(uid)
                .flatMap { result ->
                    getNextStepRx(result)
                }
                .subscribe { nextStep ->
                    when (nextStep) {
                        is RegisterUser -> bookUpdateObserver.postValue(RegisterUser)
//                        is ReadingUpdate -> firestore.getUserInfoFromUID(uid)
                        is ReadingUpdate -> finishExistingUserLogin(uid)

                    }
                }
            disposables.add(result)
        }
    }

    fun finishExistingUserLogin(uid : String) {

        val result = firestore.getUserInfoFromUID(uid)
            .andThen(firestore.getAllBooksReading())
            .subscribe({bookList ->
                bookUpdateObserver.postValue(ReadingUpdate(bookList))
            },{error ->
                Log.d("mycamera","something went wrong logging in")
            })

        disposables.add(result)
    }

    private fun getNextStepRx(result: Boolean) : Single<BookUpdate>{

        if (result) {
            return Single.just(ReadingUpdate(emptyList()))
        } else {
            return Single.just(RegisterUser)
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

        disposables.add(result)
    }


    fun takePicture() {
        cameraObserver.postValue(CameraStart)
    }
}