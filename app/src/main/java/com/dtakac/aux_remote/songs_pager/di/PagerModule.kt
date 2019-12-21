package com.dtakac.aux_remote.songs_pager.di

import com.dtakac.aux_remote.data.AppDatabase
import com.dtakac.aux_remote.songs_pager.view_model.SongsPagerViewModel
import com.dtakac.aux_remote.songs_pager.all_songs.AllSongsController
import com.dtakac.aux_remote.songs_pager.all_songs.AllSongsInterface
import com.dtakac.aux_remote.songs_pager.queue.QueueController
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val pagerModule = module {
    factory {QueueController()}
    single{get<AppDatabase>().songDao()}
    single{get<AppDatabase>().queuedSongDao()}
    single{get<AppDatabase>().nowPlayingSongDao()}
    viewModel {
        SongsPagerViewModel(
            get(),
            get(),
            get(),
            get()
        )
    }
    factory {
            (i: AllSongsInterface) -> AllSongsController(i)
    }
}