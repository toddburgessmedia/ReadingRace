package com.toddburgessmedia.mycameraapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApplication : Application() {

    val modules = module {
        viewModel { CameraViewModel(this@MyApplication) }
    }


    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(modules)
        }

    }
}