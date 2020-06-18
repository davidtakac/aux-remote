package com.dtakac.aux_remote.common.repository

import androidx.lifecycle.LiveData
import com.dtakac.aux_remote.common.model.NowPlayingSong
import com.dtakac.aux_remote.common.model.QueuedSong
import com.dtakac.aux_remote.common.model.Song
import com.dtakac.aux_remote.common.database.AppDatabase
import com.dtakac.aux_remote.common.model.Message

class AuxDatabaseRepository(
    private val db: AppDatabase
): DatabaseRepository{

    override fun clearData() {
        db.clearAllTables()
    }

    override fun insertSongs(body: List<String>) {
        db.songDao().insertAll(body.map { Song(name = it) }.toList())
    }

    override fun insertQueuedSongs(body: List<String>) {
        val result = mutableListOf<QueuedSong>()
        for(i in body.indices step 2){
            val name = body[i]
            val ownerId = body[i+1]
            result.add(QueuedSong(ownerId, name, i / 2))
        }
        db.queuedSongDao().insertAllOrUpdate(result)
    }

    override fun insertQueuedSong(body: List<String>) {
        val songName = body[0]
        val ownerId = body[1]
        val position = body[2].toInt()
        val queuedSong = QueuedSong(ownerId, songName, position)
        db.queuedSongDao().insertOrUpdate(queuedSong)
    }

    override fun updateNowPlayingSong(body: List<String>) {
        val songName = body[0]
        val ownerId = body[1]
        val nowPlayingSong = NowPlayingSong(name = songName, ownerId = ownerId)
        db.nowPlayingSongDao().setNowPlayingSong(nowPlayingSong)
    }

    override fun moveUp() {
        db.queuedSongDao().apply {
            deleteFirst()
            decrementPosition()
        }
    }

    override fun updateMessage(message: String) {
        db.messageDao().setMessage(Message(message = message))
    }

    override fun getSongs(): LiveData<List<Song>> {
        return db.songDao().getAll()
    }

    override fun getQueuedSongs(): LiveData<List<QueuedSong>> {
        return db.queuedSongDao().getQueuedSongs()
    }

    override fun getNowPlayingSong(): LiveData<NowPlayingSong> {
        return db.nowPlayingSongDao().getNowPlayingSong()
    }

    override fun getMessage(): LiveData<Message> {
        return db.messageDao().getMessage()
    }
}