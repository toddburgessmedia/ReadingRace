package com.toddburgessmedia.mycameraapp

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApplication : Application() {

    val modules = module {
        viewModel { CameraViewModel(this@MyApplication) }
        single { FirebaseAuth.getInstance() }
        single {FirebaseAnalytics.getInstance(this@MyApplication)}
    }


    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(modules)
        }

    }
}