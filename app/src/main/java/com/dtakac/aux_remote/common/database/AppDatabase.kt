package com.dtakac.aux_remote.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dtakac.aux_remote.common.model.message.Message
import com.dtakac.aux_remote.common.model.message.MessageDao
import com.dtakac.aux_remote.common.model.now_playing_song.NowPlayingSong
import com.dtakac.aux_remote.common.model.now_playing_song.NowPlayingSongDao
import com.dtakac.aux_remote.common.model.queued_song.QueuedSong
import com.dtakac.aux_remote.common.model.queued_song.QueuedSongDao
import com.dtakac.aux_remote.common.model.song.Song
import com.dtakac.aux_remote.common.model.song.SongDao

@Database(entities = [Song::class, QueuedSong::class, NowPlayingSong::class, Message::class], version = 12)
abstract class AppDatabase: RoomDatabase(){
    abstract fun songDao(): SongDao
    abstract fun queuedSongDao(): QueuedSongDao
    abstract fun nowPlayingSongDao(): NowPlayingSongDao
    abstract fun messageDao(): MessageDao
}