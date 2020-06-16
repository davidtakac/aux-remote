package com.dtakac.aux_remote.main.queue.wrapper

data class QueuedSongWrapper(
    val ownerId: String,
    val name: String,
    var position: Int,
    val userIconVisibility: Int,
    var expanded: Boolean = false
)