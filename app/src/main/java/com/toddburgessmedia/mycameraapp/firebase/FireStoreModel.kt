package com.toddburgessmedia.mycameraapp.firebase

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.toddburgessmedia.mycameraapp.CameraViewModel
import com.toddburgessmedia.mycameraapp.model.NewUser
import com.toddburgessmedia.mycameraapp.model.User
import io.reactivex.Single
import io.reactivex.SingleObserver
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class FireStoreModel(val db : FirebaseFirestore) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

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

    fun createUser(user: User) : Single<Boolean> {

        var success = false

        return Single.create { emitter ->

            launch {
                if (user.uid != null) {
                    val doc = db.collection("readers").document(user.uid)
                    try {
                        Tasks.await(doc.set(user))
                        Log.d("mycamera", "it worked")
                        emitter.onSuccess(true)
                    } catch (e: Throwable) {
                        Log.d("mycamera", "failed to create user ${e.toString()}")
                        emitter.onError(e)
                    }
                }
            }
        }

    }


}