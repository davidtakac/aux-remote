package com.dtakac.aux_remote.songs_pager.queue.wrapper

data class QueuedSongWrapper(
    val ownerId: String,
    val name: String,
    val position: Int,
    val userIconVisibility: Int
)