package com.dtakac.aux_remote.common.repository

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.dtakac.aux_remote.common.base.prefs.SharedPrefsRepository
import com.dtakac.aux_remote.common.constants.PREFS_USER_ID
import com.dtakac.aux_remote.common.model.NowPlayingSong
import com.dtakac.aux_remote.common.model.QueuedSong
import com.dtakac.aux_remote.common.model.Song
import com.dtakac.aux_remote.pager.songs.wrapper.SongWrapper
import com.dtakac.aux_remote.pager.queue.wrapper.NowPlayingSongWrapper
import com.dtakac.aux_remote.pager.queue.wrapper.QueuedSongWrapper
import com.dtakac.aux_remote.common.dao.*
import com.dtakac.aux_remote.common.model.Message

class AuxRepository(
    private val songDao: SongDao,
    private val queuedDao: QueuedSongDao,
    private val nowPlayingDao: NowPlayingSongDao,
    private val messageDao: MessageDao,
    private val sharedPrefsRepo: SharedPrefsRepository
): Repository{

    override fun insertSongs(body: List<String>) {
        songDao.insertAll(body.map { Song(name = it) }.toList())
    }

    override fun insertQueuedSongs(body: List<String>) {
        val result = mutableListOf<QueuedSong>()
        for(i in body.indices step 2){
            val name = body[i]
            val ownerId = body[i+1]
            result.add(QueuedSong(ownerId, name, i / 2))
        }
        queuedDao.insertAllOrUpdate(result)
    }

    override fun insertQueuedSong(body: List<String>) {
        val songName = body[0]
        val ownerId = body[1]
        val position = body[2].toInt()
        val queuedSong = QueuedSong(ownerId, songName, position)
        queuedDao.insertOrUpdate(queuedSong)
    }

    override fun updateNowPlayingSong(body: List<String>) {
        val songName = body[0]
        val ownerId = body[1]
        val nowPlayingSong =
            NowPlayingSong(name = songName, ownerId = ownerId)
        nowPlayingDao.setNowPlayingSong(nowPlayingSong)
    }

    override fun moveUp() {
        queuedDao.apply {
            deleteFirst()
            decrementPosition()
        }
    }

    override fun updateMessage(message: String) {
        messageDao.setMessage(Message(message = message))
    }

    override fun getSongs(): LiveData<List<SongWrapper>> {
        return Transformations.map(songDao.getAll()) {
            it?.map { song ->
                SongWrapper(song.id!!, song.name, SongWrapper.NO_HIGHLIGHT, SongWrapper.NO_COLOR)
            }?.toList()
        }
    }

    override fun getQueuedSongs(): LiveData<List<QueuedSongWrapper>> {
        return Transformations.map(queuedDao.getQueuedSongs()) {
            it?.map { song ->
                QueuedSongWrapper(song.ownerId, song.name, song.position + 1, getUserIconVisibility(song.ownerId))
            }?.toList()
        }
    }

    override fun getNowPlayingSong(): LiveData<NowPlayingSongWrapper> {
        return Transformations.map(nowPlayingDao.getNowPlayingSong()) {
            it?.let {
                NowPlayingSongWrapper(
                    it.name, it.ownerId,
                    sharedPrefsRepo.get(PREFS_USER_ID, "") == it.ownerId
                )
            }
        }
    }

    override fun getMessage(): LiveData<String> {
        return Transformations.map(messageDao.getMessage()) { it?.message }
    }

    private fun getUserIconVisibility(ownerId: String) =
        if(ownerId == sharedPrefsRepo.get(PREFS_USER_ID, "")) View.VISIBLE else View.INVISIBLE
}