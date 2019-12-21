package com.dtakac.aux_remote.repository

import com.dtakac.aux_remote.songs_pager.all_songs.SongWrapper
import io.reactivex.Observable

interface Repository {
    fun persistSongs(body: List<String>)
    fun persistQueuedSongs(body: List<String>)
    fun persistQueuedSong(body: List<String>)
    fun persistNowPlayingSong(body: List<String>)
    fun moveUp()

    fun getSongs(): Observable<List<SongWrapper>>
}