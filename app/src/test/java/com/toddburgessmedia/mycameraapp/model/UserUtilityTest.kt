package com.toddburgessmedia.mycameraapp.model

import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito

class UserUtilityTest {


    @Test
    fun testAllNulls() {

        val nullDoc = Mockito.mock(DocumentSnapshot::class.java)
        Mockito.`when`(nullDoc.get("uid")).thenReturn(null)
        Mockito.`when`(nullDoc.get("email")).thenReturn(null)
        Mockito.`when`(nullDoc.get("name")).thenReturn(null)
        Mockito.`when`(nullDoc.get("booksRead")).thenReturn(null)
        Mockito.`when`(nullDoc.get("booksReading")).thenReturn(null)

        val userTest = UserUtility.createUser(nullDoc)

        assertNotNull(userTest)

    }

}