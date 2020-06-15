package com.dtakac.aux_remote.common.repository

import androidx.lifecycle.LiveData
import com.dtakac.aux_remote.main.songs.wrapper.SongWrapper
import com.dtakac.aux_remote.main.queue.wrapper.NowPlayingSongWrapper
import com.dtakac.aux_remote.main.queue.wrapper.QueuedSongWrapper

interface Repository {
    fun clearData()

    fun insertSongs(body: List<String>)
    fun insertQueuedSongs(body: List<String>)
    fun insertQueuedSong(body: List<String>)
    fun updateNowPlayingSong(body: List<String>)
    fun moveUp()
    fun updateMessage(message: String)

    fun getSongs(): LiveData<List<SongWrapper>>
    fun getQueuedSongs(): LiveData<List<QueuedSongWrapper>>
    fun getNowPlayingSong(): LiveData<NowPlayingSongWrapper>
    fun getMessage(): LiveData<String>
}