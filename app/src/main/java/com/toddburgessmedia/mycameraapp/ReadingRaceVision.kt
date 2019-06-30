package com.toddburgessmedia.mycameraapp

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.toddburgessmedia.mycameraapp.model.CameraFail
import io.reactivex.Maybe

class ReadingRaceVision {

    fun getBarCode(bitMap: Bitmap) : Maybe<String> {

        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(
                FirebaseVisionBarcode.FORMAT_EAN_13
            ).build()

        Log.d("mycamera", "processing Image")

        val image = FirebaseVisionImage.fromBitmap(bitMap)

        val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)

        return Maybe.create { emitter ->
            image?.let { visionimage ->
                val result = detector.detectInImage(visionimage)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.size > 0) {
                            barcodes[0]?.rawValue?.let {
                                //getBookInfo(it)
                                //firebaseConversion(it)
                                emitter.onSuccess(it)
                            }
                        }
                    }
                    .addOnFailureListener {
                        emitter.onError(it)
                    }
                    .addOnCompleteListener {
                        detector.close()
                    }
            }
        }

    }


}