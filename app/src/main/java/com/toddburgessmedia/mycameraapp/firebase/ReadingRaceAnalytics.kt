package com.toddburgessmedia.mycameraapp.firebase

import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

class ReadingRaceAnalytics(val analytics : FirebaseAnalytics) {

    fun reportBookScan(isbn : String) : Maybe<String> {


        return Maybe.create {emitter ->
                analytics.logEvent("scan_book", null)
            emitter.onSuccess(isbn)
        }
    }


}