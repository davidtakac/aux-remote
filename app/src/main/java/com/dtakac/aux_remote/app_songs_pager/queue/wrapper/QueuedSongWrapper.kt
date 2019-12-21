package com.dtakac.aux_remote.app_songs_pager.queue.wrapper

data class QueuedSongWrapper(
    val ownerId: String,
    val name: String,
    val position: Int,
    val userIconVisibility: Int
)