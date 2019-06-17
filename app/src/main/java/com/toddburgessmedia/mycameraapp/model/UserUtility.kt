package com.toddburgessmedia.mycameraapp.model

import com.google.firebase.firestore.DocumentSnapshot

object UserUtility {

        fun createUser(doc: DocumentSnapshot) : User {

            val uid = doc.get("uid").toString()
            val email = doc.get("email").toString()
            val name = doc.get("name").toString()
            val booksRead = doc.get("booksRead").toString().toInt()
            val booksReading = doc.get("booksReading") as? List<String>

            return User (uid,email,name,booksRead,booksReading)

        }

}