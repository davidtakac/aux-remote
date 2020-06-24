package com.dtakac.aux_remote.common.notification

import com.dtakac.aux_remote.common.model.NowPlayingSong

interface NotificationHelper {
    fun showNowPlayingSongNotification(song: NowPlayingSong)
    fun dismissNowPlayingSongNotification()
}