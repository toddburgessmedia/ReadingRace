package com.toddburgessmedia.mycameraapp

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.toddburgessmedia.mycameraapp.firebase.FCMManager
import com.toddburgessmedia.mycameraapp.firebase.FireStoreModel
import com.toddburgessmedia.mycameraapp.firebase.ReadingRaceAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.mockito.Mockito

class MyTestApplication : Application() {
    val modules = module {
        single { Mockito.mock(FirebaseAuth::class.java) }
        single { FirebaseAnalytics.getInstance(this@MyTestApplication)}
        single { Mockito.mock(FirebaseFirestore::class.java)}
        single { FirebaseMessaging.getInstance()}

        single { FireStoreModel(get()) }
        single { FCMManager(get()) }
        single { ReadingRaceAnalytics(get()) }
        viewModel { CameraViewModel(get(),get()) }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyTestApplication)
            modules(modules)
        }

    }


}