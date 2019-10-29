package com.dtakac.aux_remote.songs_pager.queue

import android.view.View

data class QueueUi(
    val nowPlayingSongName: String,
    val queuedSongs: List<QueuedSong>
)

data class QueuedSong(
    val songName: String,
    val userIconVisibility: Int = View.GONE
)