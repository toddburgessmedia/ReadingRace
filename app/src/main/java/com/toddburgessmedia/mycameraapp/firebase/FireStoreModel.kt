package com.toddburgessmedia.mycameraapp.firebase

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.toddburgessmedia.mycameraapp.CameraViewModel
import com.toddburgessmedia.mycameraapp.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleObserver
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class FireStoreModel(val db : FirebaseFirestore) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    var viewModel : CameraViewModel? = null

    lateinit var currentUser : User


    fun userExists(uid : String) {


        val doc = db.collection("readers").document(uid)

        doc.get()
            .addOnSuccessListener { task ->
                if (task.exists()) {
                    getUserInfoFromUID(uid)
                } else {
                    viewModel?.getNextLoginStep(RegisterUser)
                }
            }
    }

    fun createUser(user: User) {

        var success = false

        if (user.uid != null) {
            val doc = db.collection("readers").document(user.uid)
            doc.set(user)
                .addOnSuccessListener {
                    viewModel?.getNextLoginStep(NewUser)
                }
        }
    }

    fun writeBookForUser(book : Book) {

        val bookDB = db.collection("books")

        currentUser.uid?.let {
            val userDB = db.collection("readers").document(it)
        }

        bookDB.add(book)
            .addOnSuccessListener {
                viewModel?.getNextLoginStep(ReadingUpdate(listOf(book)))
            }
    }


    fun getUserInfoFromUID(uid : String) {

        var email : String?
        var name : String?
        var booksRead : Int
        var user : User? = null

        val userDB = db.collection("readers").document(uid)

            userDB.get()
                .addOnSuccessListener { task ->
                    email = task.get("email").toString()
                    name = task.get("name").toString()
                    booksRead = task.get("booksRead").toString().toInt()
                    currentUser = User(uid, email, name, booksRead)
                    viewModel?.getNextLoginStep(NewUser)
                }

    }
}