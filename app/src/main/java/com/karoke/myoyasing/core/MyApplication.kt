package com.karoke.myoyasing.core

import android.app.Application
import com.karoke.myoyasing.data.local.AppDataStore
import com.karoke.myoyasing.di.module.ApiModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppDataStore.initialize(this)

        initializeKoin()
    }

    private fun initializeKoin() {
        startKoin {

            androidContext(this@MyApplication)

            modules(
                ApiModules
            )
        }
    }
}