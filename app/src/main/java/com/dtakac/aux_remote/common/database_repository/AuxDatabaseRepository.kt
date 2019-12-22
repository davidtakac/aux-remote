package com.dtakac.aux_remote.common.database_repository

import android.view.View
import com.dtakac.aux_remote.base.prefs.SharedPrefsRepository
import com.dtakac.aux_remote.common.constants.PREFS_USER_ID
import com.dtakac.aux_remote.common.extensions.defaultSchedulers
import com.dtakac.aux_remote.common.extensions.moveUp
import com.dtakac.aux_remote.common.model.now_playing_song.NowPlayingSong
import com.dtakac.aux_remote.common.model.now_playing_song.NowPlayingSongDao
import com.dtakac.aux_remote.common.model.queued_song.QueuedSong
import com.dtakac.aux_remote.common.model.queued_song.QueuedSongDao
import com.dtakac.aux_remote.common.model.song.Song
import com.dtakac.aux_remote.common.model.song.SongDao
import com.dtakac.aux_remote.app_songs_pager.all_songs.wrapper.SongWrapper
import com.dtakac.aux_remote.app_songs_pager.queue.wrapper.NowPlayingSongWrapper
import com.dtakac.aux_remote.app_songs_pager.queue.wrapper.QueuedSongWrapper
import com.dtakac.aux_remote.common.model.message.Message
import com.dtakac.aux_remote.common.model.message.MessageDao
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
            result.add(QueuedSong(ownerId, name, i/2))
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
        val nowPlayingSong = NowPlayingSong(name = songName, ownerId = ownerId)
        nowPlayingDao.setNowPlayingSong(nowPlayingSong)
    }

    override fun moveUp() {
        queuedDao.moveUp()
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

    override fun getNowPlayingSong(): Observable<NowPlayingSongWrapper> =
        nowPlayingDao.getNowPlayingSong().defaultSchedulers()
            .map {
                NowPlayingSongWrapper(
                    it.name,
                    it.ownerId,
                    sharedPrefsRepo.get(PREFS_USER_ID, "") == it.ownerId
                )
            }

    override fun getMessage(): Observable<String> =
        messageDao.getMessage().defaultSchedulers().map { it.message }

    private fun getUserIconVisibility(ownerId: String) =
        if(ownerId == sharedPrefsRepo.get(PREFS_USER_ID, "")) View.VISIBLE else View.INVISIBLE
}