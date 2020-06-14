package com.dtakac.aux_remote.main.di

import com.dtakac.aux_remote.main.view_model.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    viewModel { MainViewModel(get()) }
}