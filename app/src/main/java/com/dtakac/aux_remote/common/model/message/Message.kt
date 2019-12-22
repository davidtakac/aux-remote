package com.dtakac.aux_remote.common.model.message

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_table")
data class Message(
    @PrimaryKey val id: Int = 1,
    val message: String = ""
)