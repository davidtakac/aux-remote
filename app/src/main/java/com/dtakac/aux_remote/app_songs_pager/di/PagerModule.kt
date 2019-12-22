package com.dtakac.aux_remote.app_songs_pager.di

import com.dtakac.aux_remote.common.database.AppDatabase
import com.dtakac.aux_remote.app_songs_pager.view_model.SongsPagerViewModel
import com.dtakac.aux_remote.app_songs_pager.all_songs.fragment.AllSongsController
import com.dtakac.aux_remote.app_songs_pager.all_songs.fragment.AllSongsInterface
import com.dtakac.aux_remote.app_songs_pager.queue.fragment.QueueController
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