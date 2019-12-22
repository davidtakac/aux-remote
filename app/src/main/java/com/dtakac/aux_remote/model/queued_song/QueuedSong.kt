package com.dtakac.aux_remote.model.queued_song

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "queued_song_table")
data class QueuedSong(
    @PrimaryKey var ownerId: String,
    var name: String,
    val position: Int
)