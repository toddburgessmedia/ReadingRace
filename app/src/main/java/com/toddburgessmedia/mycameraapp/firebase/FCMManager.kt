package com.toddburgessmedia.mycameraapp.firebase

import com.google.firebase.messaging.FirebaseMessaging

class FCMManager(val fireBaseMessaging : FirebaseMessaging) {


    fun addAllSubcriptions() {

        fireBaseMessaging.subscribeToTopic("newbook")

    }

}