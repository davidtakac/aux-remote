package com.dtakac.aux_remote.di

import com.dtakac.aux_remote.common.AuxRouter
import com.dtakac.aux_remote.base.Router
import com.dtakac.aux_remote.network.ServerSocket
import org.koin.dsl.module

val appModule = module {
    single{ ServerSocket() }
    single<Router>{ AuxRouter() }
}