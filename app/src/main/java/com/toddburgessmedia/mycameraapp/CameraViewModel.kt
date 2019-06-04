package com.toddburgessmedia.mycameraapp

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.toddburgessmedia.mycameraapp.model.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CameraViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    var cameraBarCode = MutableLiveData<String>()
    var bookObserver = MutableLiveData<Book>()

    var bookUpdateObserver = MutableLiveData<BookUpdate>()

    val db : FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO



    public fun getBarCodes() = cameraBarCode

    public fun getBookInfo(isbn: String): Book? {

        val bookFactory = BookFactory.makeRetrofitService()
        var book: Book?

        launch {
            val isbnQuery = "ISBN:" + isbn
            val request = bookFactory.getBookInfo(isbn).await()
            book = request
            book?.let {
                val bookUpdate = NewBook(book)
                bookObserver.postValue(book)
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
                    Log.d("mycamera", "We failed")
                }
                .addOnCanceledListener {
                    Log.d("mycamera", "cancelled")
                }
                .addOnCompleteListener {
                    Log.d("mycamera", "complete")
                }
        }

    }

    fun firebaseConversion(isbn : String) {

        val firebaseAnalytics = FirebaseAnalytics.getInstance(getApplication())

        launch {

            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID,isbn)

            firebaseAnalytics.logEvent("scan_book",bundle)

        }
    }

    fun userExists(uid : String?) : Boolean {

        var found = false

        val doc = db.collection("readers").whereEqualTo("uid",uid)

        doc.get()
            .addOnSuccessListener { document ->
                document?.let {
                    if (!document.isEmpty) {
                        found = true
                    } else {
                    }
                }
            }

        return found
    }

    fun createUser(user: User) {

        user.uid?.let {
            val doc = db.collection("readers").document(user.uid)
                .set(user)
                .addOnSuccessListener {
                    Log.d("mycamera", "new user created")
                    bookUpdateObserver.postValue(NewUser)
                }
                .addOnFailureListener { Log.d("mycamera", "failed to create user") }
        }
    }


}