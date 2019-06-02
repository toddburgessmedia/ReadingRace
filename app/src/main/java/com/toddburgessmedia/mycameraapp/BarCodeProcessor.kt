package com.toddburgessmedia.mycameraapp

import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage

class BarCodeProcessor {

    val options = FirebaseVisionBarcodeDetectorOptions.Builder()
        .setBarcodeFormats(
            FirebaseVisionBarcode.FORMAT_EAN_13
        ).build()

    var image : FirebaseVisionImage? = null

//    fun processImage(photoResult: PhotoResult?) : String {
//
//        var barCode : String? = ""
//
//        Log.d("mycamera","processing Image")
//        photoResult?.let {
//            it.toBitmap()
//                .whenAvailable { bitmapPhoto ->
//                    Log.d("mycamera","image available")
//                    bitmapPhoto?.let { photo ->
//                        image = FirebaseVisionImage.fromBitmap(bitmapPhoto.bitmap)
//                    }
//                    val detector = FirebaseVision.getInstance()
//                        .visionBarcodeDetector
//
//                    Log.d("mycamera","detecting barcodes")
//                    image?.let { visionimage ->
//                        Log.d("mycamera","detecting barcode in image")
//                        val result = detector.detectInImage(visionimage)
//                            .addOnSuccessListener { barcodes ->
//                                Log.d("mycamera", "We found something")
//                                Log.d("mycamera",barcodes[0].rawValue)
//                                barCode = barcodes[0].rawValue
//                            }
//                            .addOnFailureListener {
//                                Log.d("mycamera","We failed")
//                                barCode = ""
//                            }
//                            .addOnCanceledListener {
//                                Log.d ("mycamera", "cancelled")
//                            }
//                            .addOnCompleteListener {
//                                Log.d("mycamera", "complete")
//                            }
//                    }
//
//                }
//        }
//
//        return barCode ?: ""
//    }

}