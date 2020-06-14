package com.dtakac.aux_remote.pager.di

import com.dtakac.aux_remote.common.database.AppDatabase
import com.dtakac.aux_remote.pager.queue.controller.QueueController
import com.dtakac.aux_remote.pager.view_model.SongsPagerViewModel
import com.dtakac.aux_remote.pager.songs.fragment.AllSongsController
import com.dtakac.aux_remote.pager.songs.fragment.AllSongsInterface
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val pagerModule = module {
    factory { QueueController() }
    single{get<AppDatabase>().songDao()}
    single{get<AppDatabase>().queuedSongDao()}
    single{get<AppDatabase>().nowPlayingSongDao()}
    single{get<AppDatabase>().messageDao()}
    viewModel {
        SongsPagerViewModel(
            get(),
            get(),
            get(),
            get()
        )
    }
    factory {
            (i: AllSongsInterface) ->
        AllSongsController(i)
    }
}