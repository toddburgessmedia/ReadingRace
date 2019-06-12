package com.toddburgessmedia.mycameraapp

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.toddburgessmedia.mycameraapp.firebase.FireStoreModel
import com.toddburgessmedia.mycameraapp.model.*
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
                firestore.writeBookForUser(it)

            }

        }
        return null
    }

    fun getBarCode(bitMap: Bitmap) {

        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(
                FirebaseVisionBarcode.FORMAT_EAN_13
            ).build()

        Log.d("mycamera", "processing Image")

        val image = FirebaseVisionImage.fromBitmap(bitMap)

        val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
        image?.let { visionimage ->
            val result = detector.detectInImage(visionimage)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.size > 0) {
                        barcodes[0]?.rawValue?.let {
                            getBookInfo(it)
                            firebaseConversion(it)
                        }
                    }
                }
                .addOnFailureListener {
                    cameraObserver.postValue(CameraFail)
                }
                .addOnCanceledListener {
                    cameraObserver.postValue(CameraFail)
                }
                .addOnCompleteListener {
                    Log.d("mycamera", "complete")
                    detector.close()
                }
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
            firestore.userExists(uid)
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

        firestore.createUser(user)
    }


    fun takePicture() {
        cameraObserver.postValue(CameraStart)
    }
}