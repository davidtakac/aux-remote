package com.dtakac.aux_remote.data.now_playing_song

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "now_playing_song_table")
data class NowPlayingSong(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val isUserSong: Boolean = false
)