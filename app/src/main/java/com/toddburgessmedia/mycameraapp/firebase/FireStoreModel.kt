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


    fun userExists(uid: String) {


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

    fun userExistsRx(uid: String) : Single<Boolean> {


        val doc = db.collection("readers").document(uid)

        return Single.create { single ->
            doc.get()
                .addOnSuccessListener { task ->
                    if (task.exists()) {
                        //getUserInfoFromUID(uid)
                        single.onSuccess(true)
                    } else {
                        //viewModel?.getNextLoginStep(RegisterUser)
                        single.onSuccess(false)
                    }
                }
        }
    }


    fun createUser(user: User) {

        if (user.uid != null) {
            val doc = db.collection("readers").document(user.uid)
            doc.set(user)
                .addOnSuccessListener {
                    viewModel?.getNextLoginStep(NewUser)
                }
        }
    }

    fun createUserRx(user: User) : Completable {

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

    fun writeBookForUser(book: Book) {

        val id = book.items[0].id

        var bookDB: DocumentReference? = null

        id?.let {
            bookDB = db.collection("books").document(id)
        }


        bookDB?.let { docRef ->
            docRef.get()
                .addOnSuccessListener { doc ->
                    if (!doc.exists()) {
                        docRef.set(book)
                            .addOnSuccessListener {
                                addBooktoUser(book)
                            }
                    } else {
                        addBooktoUser(book)
                    }
                }
        }
    }

    fun writeBookForUserRx(book: Book) : Maybe<Book> {

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

    fun addBooktoUser(book: Book) {

        val id = book.items[0].id
        val uid = currentUser?.uid
        var userDB: DocumentReference? = null

        uid?.let {
            userDB = db.collection("readers").document(uid)
        }

        userDB?.let { docRef ->
            docRef.update("booksReading", FieldValue.arrayUnion(id))
                .addOnSuccessListener {
                    //viewModel?.getNextLoginStep(ReadingUpdate(listOf(book)))
                    getAllBooksReading()
                }
        }


    }

    fun addBooktoUserRx(book: Book) : Completable {

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
                        //viewModel?.getNextLoginStep(ReadingUpdate(listOf(book)))
                        emitter.onComplete()
                        Log.d("mycamera","book added to user")
                        //getAllBooksReading()
                    }
                    .addOnFailureListener {
                        Log.d("mycamera","can't add to booksReading")
                        emitter.onError(Throwable("unable to ad to booksReading"))
                    }
            }
        }


    }


    fun getUserInfoFromUID(uid: String?) {

        var userDB: DocumentReference? = null


        uid?.let {
            userDB = db.collection("readers").document(uid)
            userReference = db.collection("readers").document(uid)
        }

        userDB?.let {
            it.get()
                .addOnSuccessListener { task ->
                    //currentUser = UserUtility.createUser(task)
                    userReference?.addSnapshotListener { snapshot, e ->
                        Log.d("mycamera", "user record updated ${snapshot?.data}")
                        snapshot?.let {
                            currentUser = UserUtility.createUser(snapshot)
                        }
                    }

                    currentUser = UserUtility.createUser(task)
                    //viewModel?.getNextLoginStep(NewUser)
                    getAllBooksReading()
                }
        }
    }

    fun getAllBooksReading() {


        val bookListRef = db.collection("books")
        for (book in currentUser?.booksReading.orEmpty()) {
            Log.d("mycamera","${book}")
            bookListRef.whereEqualTo("id",book)
        }

        bookListRef.get()
            .addOnSuccessListener { querySnapShot ->
                val bookList = mutableListOf<Book>()
                var book : Book?

                for (snap in querySnapShot) {
                    Log.d("mycamera","processing book list")
                    book = snap.toObject(Book::class.java)
                    bookList.add(book)
                }
                viewModel?.getNextLoginStep(ReadingUpdate(bookList))
            }

    }

    fun getAllBooksReadingRx() : Maybe<List<Book>> {


        val bookListRef = db.collection("books")
        for (book in currentUser?.booksReading.orEmpty()) {
            Log.d("mycamera","${book}")
            bookListRef.whereEqualTo("id",book)
        }

        return Maybe.create { emitter ->
            bookListRef.get()
                .addOnSuccessListener { querySnapShot ->
                    val bookList = mutableListOf<Book>()
                    var book: Book?

                    for (snap in querySnapShot) {
                        Log.d("mycamera", "processing book list")
                        book = snap.toObject(Book::class.java)
                        bookList.add(book)
                    }
                    //viewModel?.getNextLoginStep(ReadingUpdate(bookList))
                    emitter.onSuccess(bookList)

                }
        }

    }
}
