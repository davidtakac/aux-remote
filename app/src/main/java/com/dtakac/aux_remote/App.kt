package com.dtakac.aux_remote

import android.app.Application
import com.dtakac.aux_remote.connect.di.connectModule
import com.dtakac.aux_remote.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application(){

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)

            modules(listOf(
                appModule,
                connectModule
                )
            )
        }
    }

}