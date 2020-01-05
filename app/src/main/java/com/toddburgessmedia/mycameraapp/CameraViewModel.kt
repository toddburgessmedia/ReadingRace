package com.toddburgessmedia.mycameraapp

import androidx.lifecycle.MutableLiveData
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.toddburgessmedia.mycameraapp.firebase.FireStoreModel
import com.toddburgessmedia.mycameraapp.firebase.ReadingRaceAnalytics
import com.toddburgessmedia.mycameraapp.firebase.ReadingRaceVision
import com.toddburgessmedia.mycameraapp.model.*
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CameraViewModel(val firestore : FireStoreModel, val analytics: ReadingRaceAnalytics) : ViewModel(), CoroutineScope {

    init {
        firestore.viewModel = this
    }

    val cameraObserver = MutableLiveData<CameraAction>()

    val bookUpdateObserver = MutableLiveData<BookUpdate>()
    val bookListFragmentObserver = MutableLiveData<ReadingUpdate>()

    val disposables = CompositeDisposable()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()

    lateinit var user: User

    override fun onCleared() {
        super.onCleared()

        coroutineContext.cancelChildren()
        disposables.dispose()
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
            .flatMap { isbn -> analytics.reportBookScan(isbn) }
            .flatMap { isbn -> getBookInfo(isbn) }
            .flatMap { book -> firestore.writeBookForUser(book) }
            .flatMapCompletable { book -> firestore.addBooktoUser(book) }
            .andThen(firestore.getAllBooksReading())
            .subscribe({ booksReading ->
                bookUpdateObserver.postValue(ReadingUpdate(booksReading))
            },{ error ->
                Log.d("mycamera", "we had an error :( ${error.localizedMessage}")
                cameraObserver.postValue(CameraFail)
            })

        disposables.add(result)
    }


    fun checkUserExists(uid : String?) {

        if (uid != null) {
            val result = firestore.userExists(uid)
                .flatMap { result -> getNextStepRx(result) }
                .subscribe { nextStep ->
                    when (nextStep) {
                        is RegisterUser -> bookUpdateObserver.postValue(RegisterUser)
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

    fun userExistsCallBack(exists : Boolean) {

    }

    fun getBookDetail(item : Item) {
        bookUpdateObserver.postValue(BookDetail(item))
    }

    fun deleteBook(item: Item) {

        val result = firestore.deleteBookForUser(item)
            .andThen(firestore.deleteBookFromReadingList(item))
            .andThen(firestore.getAllBooksReading())
            .subscribe ({ bookList ->
                bookListFragmentObserver.postValue(ReadingUpdate(bookList))},
                {
                        error -> Log.d("mycamera","could not delete ${item.id}")
                })

        disposables.add(result)
    }

}