package com.toddburgessmedia.mycameraapp.firebase

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import com.toddburgessmedia.mycameraapp.CameraViewModel
import com.toddburgessmedia.mycameraapp.model.*
import io.reactivex.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class FireStoreModel(val db : FirebaseFirestore) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    var viewModel: CameraViewModel? = null

    var userReference: DocumentReference? = null

    var currentUser : User? = null


    fun userExists(uid: String) : Single<Boolean> {

        val doc = db.collection("readers").document(uid)

        return Single.create { single ->
            doc.get()
                .addOnSuccessListener { task ->
                    if (task.exists()) {
                        single.onSuccess(true)
                    } else {
                        single.onSuccess(false)
                    }
                }
                .addOnFailureListener { t ->
                    single.onError(t)
                }
        }
    }

    fun userExists(uid : String?) : Boolean {

        var found = false

        val doc = db.collection("readers").whereEqualTo("uid",uid)

        doc.get()
            .addOnSuccessListener { document ->
                document?.let {
                    if (!document.isEmpty) {
                        viewModel?.userExistsCallBack(true)
                    } else {
                        viewModel?.userExistsCallBack(false)
                    }
                }
            }

        return found
    }


    fun createUser(user: User) : Completable {

        return Completable.create { emitter ->
            if (user.uid != null) {
                val doc = db.collection("readers").document(user.uid)

                doc.set(user)
                    .addOnSuccessListener {
                        emitter.onComplete()
                        //viewModel?.getNextLoginStep(NewUser)
                    }
                    .addOnFailureListener {
                        emitter.onError(Throwable("Error saving to Firestore"))
                    }
            } else {
                emitter.onError(Throwable("uid is null"))
            }
        }
    }

    fun writeBookForUser(book: Book) : Maybe<Book> {

        val id = book.items[0].id

        var bookDB: DocumentReference? = null

        id?.let {
            bookDB = db.collection("books").document(id)
        }


        return Maybe.create { emitter ->
            bookDB?.let { docRef ->
                docRef.get()
                    .addOnSuccessListener { doc ->
                        if (!doc.exists()) {
                            docRef.set(book)
                                .addOnSuccessListener {
                                    //addBooktoUser(book)
                                    Log.d("mycamera","book written")
                                    emitter.onSuccess(book)
                                }
                        } else {
                            emitter.onComplete()
                            //addBooktoUser(book)
                        }
                    }
            }
        }
    }


    fun addBooktoUser(book: Book) : Completable {

        val id = book.items[0].id
        val uid = currentUser?.uid
        var userDB: DocumentReference? = null

        uid?.let {
            userDB = db.collection("readers").document(uid)
        }

        return Completable.create { emitter ->
            userDB?.let { docRef ->
                docRef.update("booksReading", FieldValue.arrayUnion(id))
                    .addOnSuccessListener {
                        emitter.onComplete()
                        Log.d("mycamera","book added to user")
                    }
                    .addOnFailureListener {error ->
                        Log.d("mycamera","can't add to booksReading")
                        emitter.onError(Throwable(error))
                    }
            }
        }


    }



    fun getUserInfoFromUID(uid: String?) : Completable {

        var userDB: DocumentReference? = null


        uid?.let {
            userDB = db.collection("readers").document(uid)
            userReference = db.collection("readers").document(uid)
        }

        return Completable.create {emitter ->
            userDB?.let { doc ->
                doc.get()
                    .addOnSuccessListener { task ->
                        userReference?.addSnapshotListener { snapshot, e ->
                            Log.d("mycamera", "user record updated ${snapshot?.data}")
                            snapshot?.let {
                                currentUser = UserUtility.createUser(snapshot)
                            }
                        }

                        currentUser = UserUtility.createUser(task)
                        emitter.onComplete()
                    }
                    .addOnFailureListener {error ->
                        emitter.onError(error)

                    }
            }
        }
    }

    fun getAllBooksReading() : Single<List<Book>> {


        val bookListRef = db.collection("books")
        for (book in currentUser?.booksReading.orEmpty()) {
            Log.d("mycamera","${book}")
            bookListRef.whereEqualTo("id",book)
        }

        return Single.create { emitter ->
            bookListRef.get()
                .addOnSuccessListener { querySnapShot ->
                    val bookList = mutableListOf<Book>()
                    var book: Book?

                    for (snap in querySnapShot) {
                        Log.d("mycamera", "processing book list")
                        book = snap.toObject(Book::class.java)
                        bookList.add(book)
                    }
                    emitter.onSuccess(bookList)

                }
                .addOnFailureListener {error ->
                    emitter.onError(error)
                }
        }

    }

    fun deleteBookForUser (item : Item) : Completable {

        return Completable.create { emitter ->
            item.id?.let { book ->
                db.collection("books").document(book)
                    .delete()
                    .addOnSuccessListener {
                        emitter.onComplete()
                    }
                    .addOnFailureListener { error ->
                        emitter.onError(error)
                    }
            }
        }
    }

    fun deleteBookFromReadingList (item : Item) : Completable {

        return Completable.create {emitter ->
            item.id?.let { book ->
                val userDB = db.collection("readers").document(currentUser?.uid!!)
                userDB.update("booksReading", FieldValue.arrayRemove(item.id))
                    .addOnSuccessListener {
                        emitter.onComplete()
                    }
                    .addOnFailureListener{
                        emitter.onError(it)
                    }
            }
        }
    }
}
