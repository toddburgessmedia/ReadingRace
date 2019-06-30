package com.toddburgessmedia.mycameraapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.toddburgessmedia.mycameraapp.firebase.FireStoreModel
import com.toddburgessmedia.mycameraapp.model.*
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
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


    fun getBookInfo(isbn: String): Book? {

        val bookFactory = BookFactory.makeRetrofitService()
        var book: Book?

        launch {
            val isbnQuery = "ISBN:" + isbn
            val request = bookFactory.getBookInfo(isbn).await()
            book = request
            book?.let {
                val bookUpdate = NewBook(book)
                if (it.items.isNotEmpty()) {
                    firestore.writeBookForUser(it)
                }

            }

        }
        return null
    }

    fun getBookInfoRx(isbn: String): Maybe<Book> {

        val bookFactory = BookFactory.makeRetrofitService()

        return Maybe.create { emitter ->
            launch {
                val request = bookFactory.getBookInfo(isbn).await()
                val book = request
                val bookUpdate = NewBook(book)
                if (book.items.isNotEmpty()) {
                        //firestore.writeBookForUser(it)
                    Log.d("mycamera", "we found book ${book.toString()}")
                    emitter.onSuccess(book)
                } else {
                    emitter.onError(Throwable("No Book found"))
                }
            }
        }
    }

//    fun getBarCode(bitMap: Bitmap) {
//
//        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
//            .setBarcodeFormats(
//                FirebaseVisionBarcode.FORMAT_EAN_13
//            ).build()
//
//        Log.d("mycamera", "processing Image")
//
//        val image = FirebaseVisionImage.fromBitmap(bitMap)
//
//        val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
//        image?.let { visionimage ->
//            val result = detector.detectInImage(visionimage)
//                .addOnSuccessListener { barcodes ->
//                    if (barcodes.size > 0) {
//                        barcodes[0]?.rawValue?.let {
//                            getBookInfo(it)
//                            firebaseConversion(it)
//                        }
//                    }
//                }
//                .addOnFailureListener {
//                    cameraObserver.postValue(CameraFail)
//                }
//                .addOnCanceledListener {
//                    cameraObserver.postValue(CameraFail)
//                }
//                .addOnCompleteListener {
//                    Log.d("mycamera", "complete")
//                    detector.close()
//                }
//        }
//
//    }

    fun getBarCode(bitMap: Bitmap) {

        val vision = ReadingRaceVision()

        val result = vision.getBarCode(bitMap)
            .flatMap { isbn ->
                getBookInfoRx(isbn)
            }
            .flatMap {book ->
                firestore.writeBookForUserRx(book)
            }
            .flatMapCompletable {
                firestore.addBooktoUserRx(it)
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
            val result = firestore.userExistsRx(uid)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap { result ->
                    getNextStepRx(result)
                }
                .subscribe { nextStep ->
                    when (nextStep) {
                        is RegisterUser -> bookUpdateObserver.postValue(RegisterUser)
                        is ReadingUpdate -> firestore.getUserInfoFromUID(uid)
                    }
                }

            //result.dispose()
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

        val result = firestore.createUserRx(user)
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