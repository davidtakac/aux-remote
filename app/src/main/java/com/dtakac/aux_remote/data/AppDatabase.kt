package com.dtakac.aux_remote.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dtakac.aux_remote.data.now_playing_song.NowPlayingSong
import com.dtakac.aux_remote.data.now_playing_song.NowPlayingSongDao
import com.dtakac.aux_remote.data.queued_song.QueuedSong
import com.dtakac.aux_remote.data.queued_song.QueuedSongDao
import com.dtakac.aux_remote.data.song.Song
import com.dtakac.aux_remote.data.song.SongDao

@Database(entities = [Song::class, QueuedSong::class, NowPlayingSong::class], version = 5)
abstract class AppDatabase: RoomDatabase(){
    abstract fun songDao(): SongDao
    abstract fun queuedSongDao(): QueuedSongDao
    abstract fun nowPlayingSongDao(): NowPlayingSongDao
}