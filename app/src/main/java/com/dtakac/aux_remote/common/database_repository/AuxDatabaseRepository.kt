package com.dtakac.aux_remote.common.database_repository

import android.view.View
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.dtakac.aux_remote.common.base.prefs.SharedPrefsRepository
import com.dtakac.aux_remote.common.constants.PREFS_USER_ID
import com.dtakac.aux_remote.common.extensions.defaultSchedulers
import com.dtakac.aux_remote.common.model.NowPlayingSong
import com.dtakac.aux_remote.common.model.QueuedSong
import com.dtakac.aux_remote.common.model.Song
import com.dtakac.aux_remote.pager.songs.wrapper.SongWrapper
import com.dtakac.aux_remote.pager.queue.wrapper.NowPlayingSongWrapper
import com.dtakac.aux_remote.pager.queue.wrapper.QueuedSongWrapper
import com.dtakac.aux_remote.common.dao.*
import com.dtakac.aux_remote.common.model.Message
import io.reactivex.Observable

class AuxDatabaseRepository(
    private val songDao: SongDao,
    private val queuedDao: QueuedSongDao,
    private val nowPlayingDao: NowPlayingSongDao,
    private val messageDao: MessageDao,
    private val sharedPrefsRepo: SharedPrefsRepository
): DatabaseRepository{

    override fun persistSongs(body: List<String>) {
        songDao.insertAll(body.map { Song(name = it) }.toList())
    }

    override fun persistQueuedSongs(body: List<String>) {
        val result = mutableListOf<QueuedSong>()
        for(i in body.indices step 2){
            val name = body[i]
            val ownerId = body[i+1]
            result.add(QueuedSong(ownerId, name, i / 2))
        }
        queuedDao.insertAllOrUpdate(result)
    }

    override fun persistQueuedSong(body: List<String>) {
        val songName = body[0]
        val ownerId = body[1]
        val position = body[2].toInt()
        val queuedSong = QueuedSong(ownerId, songName, position)
        queuedDao.insertOrUpdate(queuedSong)
    }

    override fun persistNowPlayingSong(body: List<String>) {
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

    override fun persistMessage(message: String) {
        messageDao.setMessage(Message(message = message))
    }

    override fun getSongs(): Observable<List<SongWrapper>> =
        songDao.getAll().defaultSchedulers().flatMap {
            Observable.fromIterable(it)
                .map{song ->
                    SongWrapper(
                        song.id!!,
                        song.name,
                        SongWrapper.NO_HIGHLIGHT,
                        SongWrapper.NO_COLOR
                    )
                }
                .toList()
                .toObservable()
        }

    override fun getQueuedSongs(): Observable<List<QueuedSongWrapper>> =
        queuedDao.getQueuedSongs().defaultSchedulers().flatMap {
            Observable.fromIterable(it)
                .map{queued ->
                    QueuedSongWrapper(
                        queued.ownerId,
                        queued.name,
                        queued.position + 1,
                        getUserIconVisibility(queued.ownerId)
                    )
                }
                .toList()
                .toObservable()
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