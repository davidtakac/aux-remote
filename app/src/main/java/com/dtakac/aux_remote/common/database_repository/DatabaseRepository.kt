package com.dtakac.aux_remote.common.database_repository

import androidx.lifecycle.LiveData
import com.dtakac.aux_remote.pager.songs.wrapper.SongWrapper
import com.dtakac.aux_remote.pager.queue.wrapper.NowPlayingSongWrapper
import com.dtakac.aux_remote.pager.queue.wrapper.QueuedSongWrapper
import io.reactivex.Observable

interface DatabaseRepository {
    fun persistSongs(body: List<String>)
    fun persistQueuedSongs(body: List<String>)
    fun persistQueuedSong(body: List<String>)
    fun persistNowPlayingSong(body: List<String>)
    fun moveUp()
    fun persistMessage(message: String)

    fun getSongs(): Observable<List<SongWrapper>>
    fun getQueuedSongs(): Observable<List<QueuedSongWrapper>>
    fun getNowPlayingSong(): LiveData<NowPlayingSongWrapper>
    fun getMessage(): LiveData<String>
}