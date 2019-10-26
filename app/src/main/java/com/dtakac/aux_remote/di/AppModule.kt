package com.dtakac.aux_remote.di

import android.content.Context
import android.preference.PreferenceManager
import com.dtakac.aux_remote.common.AuxRouter
import com.dtakac.aux_remote.base.Router
import com.dtakac.aux_remote.base.SharedPrefsRepo
import com.dtakac.aux_remote.common.AuxSharedPrefsRepo
import com.dtakac.aux_remote.network.NetworkUtil
import com.dtakac.aux_remote.network.ClientSocket
import org.koin.dsl.module

val appModule = module {
    single{ ClientSocket() }
    single<Router>{ AuxRouter() }
    single<SharedPrefsRepo>{
        AuxSharedPrefsRepo(get<Context>().getSharedPreferences("auxprefs", Context.MODE_PRIVATE))
    }
    single{ NetworkUtil(get()) }
}