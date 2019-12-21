package com.dtakac.aux_remote.repository

import com.dtakac.aux_remote.common.defaultSchedulers
import com.dtakac.aux_remote.common.moveUp
import com.dtakac.aux_remote.data.now_playing_song.NowPlayingSong
import com.dtakac.aux_remote.data.now_playing_song.NowPlayingSongDao
import com.dtakac.aux_remote.data.queued_song.QueuedSong
import com.dtakac.aux_remote.data.queued_song.QueuedSongDao
import com.dtakac.aux_remote.data.song.Song
import com.dtakac.aux_remote.data.song.SongDao
import com.dtakac.aux_remote.songs_pager.all_songs.SongWrapper
import io.reactivex.Observable

class AuxRepository(
    private val songDao: SongDao,
    private val queuedDao: QueuedSongDao,
    private val nowPlayingDao: NowPlayingSongDao
): Repository{

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

    override fun getSongs(): Observable<List<SongWrapper>> =
        songDao.getAll().defaultSchedulers().flatMap {
            Observable.fromIterable(it)
                .map{song -> SongWrapper(song.id!!, song.name, SongWrapper.NO_HIGHLIGHT, SongWrapper.NO_COLOR)}
                .toList()
                .toObservable()
        }
}