package com.dtakac.aux_remote.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dtakac.aux_remote.common.model.Message
import com.dtakac.aux_remote.common.database.dao.MessageDao
import com.dtakac.aux_remote.common.model.NowPlayingSong
import com.dtakac.aux_remote.common.database.dao.NowPlayingSongDao
import com.dtakac.aux_remote.common.model.QueuedSong
import com.dtakac.aux_remote.common.database.dao.QueuedSongDao
import com.dtakac.aux_remote.common.model.Song
import com.dtakac.aux_remote.common.database.dao.SongDao

@Database(entities = [Song::class, QueuedSong::class, NowPlayingSong::class, Message::class], version = 12)
abstract class AppDatabase: RoomDatabase(){
    abstract fun songDao(): SongDao
    abstract fun queuedSongDao(): QueuedSongDao
    abstract fun nowPlayingSongDao(): NowPlayingSongDao
    abstract fun messageDao(): MessageDao
}