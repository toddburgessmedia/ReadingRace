package com.toddburgessmedia.mycameraapp

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.toddburgessmedia.mycameraapp.firebase.FCMManager
import com.toddburgessmedia.mycameraapp.firebase.FireStoreModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApplication : Application() {

    val modules = module {
        single { FirebaseAuth.getInstance() }
        single {FirebaseAnalytics.getInstance(this@MyApplication)}
        single {FirebaseFirestore.getInstance()}
        single {FirebaseMessaging.getInstance()}
        single {FireStoreModel(get())}
        viewModel { CameraViewModel(this@MyApplication, get()) }
        single {FCMManager(get())}
    }


    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(modules)
        }

    }
}