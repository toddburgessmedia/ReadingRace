package com.toddburgessmedia.mycameraapp

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.toddburgessmedia.mycameraapp.firebase.FireStoreModel
import com.toddburgessmedia.mycameraapp.model.*
import io.reactivex.Single
import io.reactivex.SingleObserver
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CameraViewModel(application: Application, val firestore : FireStoreModel) : AndroidViewModel(application), CoroutineScope {

    var cameraObserver = MutableLiveData<CameraAction>()

    var bookUpdateObserver = MutableLiveData<BookUpdate>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO


    public fun getBookInfo(isbn: String): Book? {

        val bookFactory = BookFactory.makeRetrofitService()
        var book: Book?

        launch {
            val isbnQuery = "ISBN:" + isbn
            val request = bookFactory.getBookInfo(isbn).await()
            book = request
            book?.let {
                val bookUpdate = NewBook(book)
                //bookObserver.postValue(book)
                bookUpdateObserver.postValue(ReadingUpdate(listOf(it)))

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

    fun userExists(uid : String?) : Boolean {

        return firestore.userExists(uid)
    }

    fun createUser(user: User) {

        val single = firestore.createUser(user)
            .doOnSuccess { b ->
                if (b == true) {
                    bookUpdateObserver.postValue(NewUser)
                }
            }
        single.subscribe()

    }

    fun notifyUserCreated() {

        bookUpdateObserver.postValue(NewUser)
    }


    fun takePicture() {
        cameraObserver.postValue(CameraStart)
    }
}