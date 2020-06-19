package com.dtakac.aux_remote.common.repository

import androidx.lifecycle.LiveData
import com.dtakac.aux_remote.common.model.NowPlayingSong
import com.dtakac.aux_remote.common.model.QueuedSong
import com.dtakac.aux_remote.common.model.Song
import com.dtakac.aux_remote.common.database.AppDatabase
import com.dtakac.aux_remote.common.model.Message
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class DatabaseRepository(
    private val db: AppDatabase
): Repository{

    override suspend fun clearData() {
        withContext(IO){ db.clearAllTables() }
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
                result.add(QueuedSong(ownerId, name, i / 2))
            }
        }
        withContext(IO){ db.queuedSongDao().insertAllOrUpdate(result) }
    }

    override suspend fun insertQueuedSong(body: List<String>) {
        val songName = body[0]
        val ownerId = body[1]
        val position = body[2].toInt()
        val queuedSong = QueuedSong(ownerId, songName, position)
        withContext(IO){ db.queuedSongDao().insertOrUpdate(queuedSong) }
    }

    override suspend fun updateNowPlayingSong(body: List<String>) {
        val songName = body[0]
        val ownerId = body[1]
        val nowPlayingSong = NowPlayingSong(name = songName, ownerId = ownerId)
        withContext(IO){ db.nowPlayingSongDao().setNowPlayingSong(nowPlayingSong) }
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