package com.dtakac.aux_remote.songs_pager.di

import com.dtakac.aux_remote.songs_pager.all_songs.AllSongsController
import com.dtakac.aux_remote.songs_pager.all_songs.AllSongsInterface
import org.koin.dsl.module

val pagerModule = module {
    factory {
        (i: AllSongsInterface) -> AllSongsController(i)
    }
}