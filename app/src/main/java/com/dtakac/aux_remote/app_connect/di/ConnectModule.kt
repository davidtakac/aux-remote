package com.dtakac.aux_remote.app_connect.di

import com.dtakac.aux_remote.app_connect.presenter.ConnectContract
import com.dtakac.aux_remote.app_connect.presenter.ConnectPresenter
import org.koin.dsl.module

val connectModule = module {
    factory<ConnectContract.Presenter>{
        (v: ConnectContract.View) ->
        ConnectPresenter(v, get(), get(), get(), get())
    }
}