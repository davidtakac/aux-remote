package com.dtakac.aux_remote.common.repository

import androidx.lifecycle.LiveData
import com.dtakac.aux_remote.common.database.AppDatabase
import com.dtakac.aux_remote.common.model.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class DatabaseRepository(
    private val db: AppDatabase
): Repository{

    override suspend fun clearPlayerSession() {
        withContext(IO){
            db.messageDao().deleteMessage()
            db.nowPlayingSongDao().deleteSong()
            db.songDao().deleteAll()
            db.queuedSongDao().deleteAll()
        }
    }

    override suspend fun insertSongs(body: List<String>) {
        withContext(IO){ db.songDao().insertAll(body.map { Song(name = it) }.toList()) }
    }

    override suspend fun insertQueuedSongs(body: List<String>) {
        val result = mutableListOf<QueuedSong>()
        withContext(Default){
            for(i in body.indices step 2){
                val name = body[i]
                val ownerId = body[i+1]
                var ownerNickname: Nickname? = null
                withContext(IO) { ownerNickname = db.nicknameDao().getNickname(ownerId) }
                result.add(QueuedSong(ownerId, name, i / 2, ownerNickname?.nickname))
            }
        }
        withContext(IO){ db.queuedSongDao().insertAllOrUpdate(result) }
    }

    override suspend fun insertQueuedSong(body: List<String>) {
        val songName = body[0]
        val ownerId = body[1]
        val position = body[2].toInt()
        withContext(IO){
            val ownerNickname = db.nicknameDao().getNickname(ownerId)
            val queuedSong = QueuedSong(ownerId, songName, position, ownerNickname?.nickname)
            db.queuedSongDao().insertOrUpdate(queuedSong)
        }
    }

    override suspend fun updateNowPlayingSong(body: List<String>): NowPlayingSong {
        val songName = body[0]
        val ownerId = body[1]
        val nowPlayingSong = NowPlayingSong(name = songName, ownerId = ownerId)
        withContext(IO){ db.nowPlayingSongDao().setNowPlayingSong(nowPlayingSong) }
        return nowPlayingSong
    }

    override suspend fun updateNickname(ownerId: String, nickname: String?) {
        withContext(IO){
            db.nicknameDao().insertOrUpdate(Nickname(ownerId, nickname))
            db.queuedSongDao().updateNickname(ownerId, nickname)
        }
    }

    override suspend fun moveUp() {
        withContext(IO){
            db.queuedSongDao().deleteFirst()
            db.queuedSongDao().decrementPosition()
        }
    }

    override suspend fun updateMessage(message: String) {
        withContext(IO){ db.messageDao().setMessage(Message(message = message)) }
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