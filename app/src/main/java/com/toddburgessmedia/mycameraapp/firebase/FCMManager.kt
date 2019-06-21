package com.toddburgessmedia.mycameraapp.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class FCMManager(val fireBaseMessaging : FirebaseMessaging) {


    fun addAllSubcriptions() {

        fireBaseMessaging.subscribeToTopic("newbook")
            .addOnSuccessListener { Log.d("mycamera", "we subscribed") }

    }

}