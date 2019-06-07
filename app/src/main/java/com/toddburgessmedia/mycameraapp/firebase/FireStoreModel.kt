package com.toddburgessmedia.mycameraapp.firebase

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.toddburgessmedia.mycameraapp.CameraViewModel
import com.toddburgessmedia.mycameraapp.model.Book
import com.toddburgessmedia.mycameraapp.model.NewUser
import com.toddburgessmedia.mycameraapp.model.User
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleObserver
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class FireStoreModel(val db : FirebaseFirestore) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    fun userExists(uid : String) : Single<Boolean> {


        val doc = db.collection("readers").document(uid)

        return Single.create { emitter ->
            launch {
                try {
                    val document = Tasks.await(doc.get())
                    if (document.exists()) {
                        emitter.onSuccess(true)
                    } else {
                        emitter.onSuccess(false)
                    }
                } catch (e : Throwable) {
                    emitter.onError(e)
                }

            }
        }
    }

    fun createUser(user: User) : Completable {

        var success = false

        return Completable.create { emitter ->

            launch {
                if (user.uid != null) {
                    val doc = db.collection("readers").document(user.uid)
                    try {
                        Tasks.await(doc.set(user))
                        emitter.onComplete()
                    } catch (e: Throwable) {
                        emitter.onError(e)
                    }
                }
            }
        }
    }

    fun writeBookForUser(user: User, book : Book) : Completable {

        val bookDB = db.collection("books")
        user.uid?.let {
            val userDB = db.collection("readers").document(user.uid)
        }

        return Completable.create {emitter ->
            launch {
                try {
                    Tasks.await(bookDB.add(book))
                    emitter.onComplete()
                } catch (t: Throwable) {
                    emitter.onError(t)
                }
            }
        }
    }

    fun getUserInfoFromUID(uid : String) : Single<User> {

        var email : String?
        var name : String?
        var booksRead : Int

        val userDB = db.collection("readers").document(uid)


        return Single.create { emitter ->
            launch {
                try {
                    val doc = Tasks.await(userDB.get())
                    email = doc.get("email").toString()
                    name = doc.get("name").toString()
                    booksRead = doc.get("booksRead").toString().toInt()
                    emitter.onSuccess(User(uid, email, name, booksRead))
                } catch (t: Throwable) {
                    emitter.onError(t)
                }

            }
        }
    }

}