package com.dtakac.aux_remote.pager.queue.controller

import com.airbnb.epoxy.EpoxyController
import com.dtakac.aux_remote.common.constants.EMPTY_STRING
import com.dtakac.aux_remote.pager.queue.view_holders.nowPlayingSong
import com.dtakac.aux_remote.pager.queue.view_holders.queuedSong
import com.dtakac.aux_remote.pager.queue.wrapper.NowPlayingSongWrapper
import com.dtakac.aux_remote.pager.queue.wrapper.QueuedSongWrapper

class QueueController: EpoxyController(){
    private var nowPlayingSong: NowPlayingSongWrapper? = null
    private var queue: List<QueuedSongWrapper>? = listOf()

    fun setQueue(songs: List<QueuedSongWrapper>?) {
        queue = songs
        requestModelBuild()
    }

    fun setNowPlayingSong(song: NowPlayingSongWrapper?){
        nowPlayingSong = song
        requestModelBuild()
    }

    override fun buildModels() {
        nowPlayingSong {
            id("now-playing")
            name(nowPlayingSong?.name ?: EMPTY_STRING)
        }
        queue?.forEach { queuedSong ->
            queuedSong {
                id(queuedSong.ownerId)
                position(queuedSong.position.toString())
                name(queuedSong.name)
                userIconVisibility(queuedSong.userIconVisibility)
            }
        }
    }
}