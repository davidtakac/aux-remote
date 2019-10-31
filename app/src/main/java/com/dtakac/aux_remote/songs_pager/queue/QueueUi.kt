package com.dtakac.aux_remote.songs_pager.queue

import com.dtakac.aux_remote.data.now_playing_song.NowPlayingSong
import com.dtakac.aux_remote.data.queued_song.QueuedSong

data class QueueUi(
    var queuedSongs: List<QueuedSong>,
    var nowPlayingSong: NowPlayingSong
)