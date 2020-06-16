package com.dtakac.aux_remote.main.queue.controller

import com.airbnb.epoxy.EpoxyController
import com.dtakac.aux_remote.common.constants.EMPTY_STRING
import com.dtakac.aux_remote.main.queue.view_holders.nowPlayingSong
import com.dtakac.aux_remote.main.queue.view_holders.queuedSong
import com.dtakac.aux_remote.main.queue.view_holders.queuedSongInfo
import com.dtakac.aux_remote.main.queue.wrapper.NowPlayingSongWrapper
import com.dtakac.aux_remote.main.queue.wrapper.QueuedSongWrapper

class QueueController: EpoxyController(){
    private var nowPlayingSong: NowPlayingSongWrapper? = null
    private var queue = mutableListOf<QueuedSongWrapper>()

    fun setQueue(songs: List<QueuedSongWrapper>) {
        queue.clear()
        queue.addAll(songs)
        requestModelBuild()
    }

    fun setNowPlayingSong(song: NowPlayingSongWrapper){
        nowPlayingSong = song
        requestModelBuild()
    }

    private fun onQueuedSongClicked(wrapper: QueuedSongWrapper){
        //toggle expansion
        val idx = queue.indexOfFirst { it.ownerId == wrapper.ownerId }
        wrapper.expanded = !wrapper.expanded
        queue[idx] = wrapper
        requestModelBuild()
    }

    override fun buildModels() {
        if(nowPlayingSong != null){
            nowPlayingSong {
                id("now-playing")
                name(nowPlayingSong!!.name)
            }
        }
        queue.forEach { wrapper ->
            queuedSong {
                id(wrapper.ownerId)
                position(wrapper.position.toString())
                name(wrapper.name)
                userIconVisibility(wrapper.userIconVisibility)
                expanded(wrapper.expanded)
                onClick { _, _, _, _ ->
                    onQueuedSongClicked(wrapper)
                }
            }
            if(wrapper.expanded){
                queuedSongInfo {
                    id("info: ${wrapper.ownerId}")
                    owner(wrapper.ownerId)
                }
            }
        }
    }
}