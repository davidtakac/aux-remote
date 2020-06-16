package com.dtakac.aux_remote.main.queue.wrapper

data class QueuedSongWrapper(
    val ownerId: String,
    val name: String,
    val position: Int,
    val userIconVisibility: Int,
    var expanded: Boolean = false
)