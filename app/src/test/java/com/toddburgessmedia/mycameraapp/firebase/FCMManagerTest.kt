package com.toddburgessmedia.mycameraapp.firebase

import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FCMManagerTest {

    @Mock
    lateinit var firebaseMessageMock : FirebaseMessaging

    @Mock
    lateinit var task : Task<Void>

    @Captor
    lateinit var testonSuccess : ArgumentCaptor<OnSuccessListener<Void>>


    lateinit var fcmMock : FCMManager

    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)

        Mockito.`when`(task.addOnSuccessListener(testonSuccess.capture())).thenReturn(task)
1
        fcmMock = FCMManager(firebaseMessageMock)

    }

    @Test
    fun checkSubscription() {

        fcmMock.addAllSubcriptions()
        verify(firebaseMessageMock).subscribeToTopic("newbook")



    }
}