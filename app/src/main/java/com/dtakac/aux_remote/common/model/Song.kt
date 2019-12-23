package com.dtakac.aux_remote.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_table")
data class Song(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var name: String
)