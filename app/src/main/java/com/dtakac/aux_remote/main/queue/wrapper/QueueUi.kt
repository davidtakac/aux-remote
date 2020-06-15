package com.dtakac.aux_remote.main.queue.wrapper

import com.dtakac.aux_remote.common.constants.EMPTY_STRING

data class QueueUi(
    var queuedSongs: List<QueuedSongWrapper>,
    var nowPlayingSong: NowPlayingSongWrapper
)

fun provideQueueUi(queue: List<QueuedSongWrapper>?, nowPlaying: NowPlayingSongWrapper?) =
    QueueUi(queue ?: listOf(), nowPlaying ?: NowPlayingSongWrapper(
        EMPTY_STRING,
        EMPTY_STRING, false))