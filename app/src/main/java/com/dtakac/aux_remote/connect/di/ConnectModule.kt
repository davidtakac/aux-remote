package com.dtakac.aux_remote.connect.di

import com.dtakac.aux_remote.connect.ConnectContract
import com.dtakac.aux_remote.connect.ConnectPresenter
import org.koin.dsl.module

val connectModule = module {
    factory<ConnectContract.Presenter>{
        (v: ConnectContract.View) -> ConnectPresenter(v, get(), get(), get(), get())
    }
}