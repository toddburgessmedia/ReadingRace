package com.toddburgessmedia.mycameraapp.model

import com.google.firebase.firestore.DocumentSnapshot

class UserUtility {

    companion object {

        fun createUser(doc: DocumentSnapshot) : User {

//            var uid : String?
//            var email: String?
//            var name: String?
//            var booksRead: Int

            val uid = doc.get("uid").toString()
            val email = doc.get("email").toString()
            val name = doc.get("name").toString()
            val booksRead = doc.get("booksRead").toString().toInt()
            val booksReading = doc.get("booksReading") as? List<String>

            return User (uid,email,name,booksRead,booksReading)

        }





    }

}