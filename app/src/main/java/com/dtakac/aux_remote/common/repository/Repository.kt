package com.dtakac.aux_remote.common.repository

import androidx.lifecycle.LiveData
import com.dtakac.aux_remote.common.model.Message
import com.dtakac.aux_remote.common.model.NowPlayingSong
import com.dtakac.aux_remote.common.model.QueuedSong
import com.dtakac.aux_remote.common.model.Song

interface Repository {
    suspend fun clearPlayerSession()

    suspend fun insertSongs(body: List<String>)
    suspend fun insertQueuedSongs(body: List<String>)
    suspend fun insertQueuedSong(body: List<String>)
    suspend fun updateNowPlayingSong(body: List<String>)
    suspend fun moveUp()
    suspend fun updateMessage(message: String)
    suspend fun updateNickname(ownerId: String, nickname: String?)

    fun getSongs(): LiveData<List<Song>>
    fun getQueuedSongs(): LiveData<List<QueuedSong>>
    fun getNowPlayingSong(): LiveData<NowPlayingSong>
    fun getMessage(): LiveData<Message>
}