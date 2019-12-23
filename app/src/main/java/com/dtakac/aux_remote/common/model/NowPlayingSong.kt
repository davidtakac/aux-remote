package com.dtakac.aux_remote.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "now_playing_song_table")
data class NowPlayingSong(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val ownerId: String = ""
)