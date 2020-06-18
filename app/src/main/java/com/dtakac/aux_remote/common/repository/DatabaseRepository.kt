package com.dtakac.aux_remote.common.repository

import androidx.lifecycle.LiveData
import com.dtakac.aux_remote.common.model.Message
import com.dtakac.aux_remote.common.model.NowPlayingSong
import com.dtakac.aux_remote.common.model.QueuedSong
import com.dtakac.aux_remote.common.model.Song

interface DatabaseRepository {
    fun clearData()

    fun insertSongs(body: List<String>)
    fun insertQueuedSongs(body: List<String>)
    fun insertQueuedSong(body: List<String>)
    fun updateNowPlayingSong(body: List<String>)
    fun moveUp()
    fun updateMessage(message: String)

    fun getSongs(): LiveData<List<Song>>
    fun getQueuedSongs(): LiveData<List<QueuedSong>>
    fun getNowPlayingSong(): LiveData<NowPlayingSong>
    fun getMessage(): LiveData<Message>
}