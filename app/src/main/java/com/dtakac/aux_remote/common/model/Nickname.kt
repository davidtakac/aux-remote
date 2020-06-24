package com.dtakac.aux_remote.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nickname_table")
data class Nickname(
    @PrimaryKey var ownerId: String,
    val nickname: String? = null
)