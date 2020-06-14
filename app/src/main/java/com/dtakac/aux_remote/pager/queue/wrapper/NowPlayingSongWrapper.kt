package com.dtakac.aux_remote.pager.queue.wrapper

data class NowPlayingSongWrapper(
    val name: String,
    val ownerId: String,
    val isUserSong: Boolean
)