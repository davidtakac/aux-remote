package com.dtakac.aux_remote.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dtakac.aux_remote.common.database.dao.*
import com.dtakac.aux_remote.common.model.*

@Database(entities = [Song::class, QueuedSong::class, NowPlayingSong::class, Message::class, Nickname::class], version = 13)
abstract class AppDatabase: RoomDatabase(){
    abstract fun songDao(): SongDao
    abstract fun queuedSongDao(): QueuedSongDao
    abstract fun nowPlayingSongDao(): NowPlayingSongDao
    abstract fun messageDao(): MessageDao
    abstract fun nicknameDao(): NicknameDao
}