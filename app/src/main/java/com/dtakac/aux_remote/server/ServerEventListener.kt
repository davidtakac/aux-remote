package com.dtakac.aux_remote.server

import com.dtakac.aux_remote.common.model.NowPlayingSong

interface ServerEventListener {
    fun onNowPlayingSongChanged(song: NowPlayingSong)
}