package com.dtakac.aux_remote.app_songs_pager.pager.wrapper

import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.app_songs_pager.queue.wrapper.QueuedSongWrapper

data class UserQueuedSongUi(
    val snackbarMessageId: Int,
    val queuedSong: QueuedSongWrapper
)

fun provideUserQueuedSongUi(swapped: Boolean, song: QueuedSongWrapper) =
    UserQueuedSongUi(
        if (swapped) R.string.snackbar_swapped else R.string.snackbar_queued,
        song
    )