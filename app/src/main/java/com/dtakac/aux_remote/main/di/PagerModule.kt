package com.dtakac.aux_remote.main.di

import com.dtakac.aux_remote.server.AuxServerInteractor
import com.dtakac.aux_remote.main.queue.controller.QueueController
import com.dtakac.aux_remote.main.songs.controller.SongsListController
import com.dtakac.aux_remote.main.songs.controller.SongsListInterface
import com.dtakac.aux_remote.main.view_model.SongsPagerViewModel
import com.dtakac.aux_remote.server.ServerInteractor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val pagerModule = module {
    single<ServerInteractor> { AuxServerInteractor(get()) }
    factory { QueueController() }
    viewModel { SongsPagerViewModel(get(), get(), get(), get()) }
    factory { (i: SongsListInterface) -> SongsListController(i) }
}