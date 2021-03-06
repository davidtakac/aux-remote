package com.dtakac.aux_remote

import android.app.Application
import com.dtakac.aux_remote.app_main.di.mainModule
import com.dtakac.aux_remote.app_connect.di.connectModule
import com.dtakac.aux_remote.common.di.appModule
import com.dtakac.aux_remote.app_songs_pager.di.pagerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(appModule, connectModule, pagerModule,
                mainModule
            ))
        }
    }
}