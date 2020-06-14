package com.dtakac.aux_remote.connect.di

import com.dtakac.aux_remote.connect.presenter.ConnectContract
import com.dtakac.aux_remote.connect.presenter.ConnectPresenter
import org.koin.dsl.module

val connectModule = module {
    factory<ConnectContract.Presenter>{
        (v: ConnectContract.View) ->
        ConnectPresenter(v, get(), get(), get(), get())
    }
}