package com.dtakac.aux_remote.main.queue.controller

import com.airbnb.epoxy.EpoxyController
import com.dtakac.aux_remote.main.queue.view_holders.nowPlayingSong
import com.dtakac.aux_remote.main.queue.view_holders.queuedSong
import com.dtakac.aux_remote.main.queue.view_holders.queuedSongInfo
import com.dtakac.aux_remote.main.queue.wrapper.NowPlayingSongWrapper
import com.dtakac.aux_remote.main.queue.wrapper.QueuedSongWrapper

class QueueController(private val queueInterface: QueueInterface): EpoxyController(){
    private var nowPlayingSong: NowPlayingSongWrapper? = null
    private var queue: List<QueuedSongWrapper> = listOf()

    fun setQueue(songs: List<QueuedSongWrapper>) {
        queue = songs
        requestModelBuild()
    }

    fun setNowPlayingSong(song: NowPlayingSongWrapper){
        nowPlayingSong = song
        requestModelBuild()
    }

    private fun onQueuedSongClicked(clickedOwnerId: String){
        //toggle expansion
        val wrapper = queue.firstOrNull { it.ownerId == clickedOwnerId } ?: return
        wrapper.expanded = !wrapper.expanded
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
                    onQueuedSongClicked(wrapper.ownerId)
                }
            }
            if(wrapper.expanded){
                queuedSongInfo {
                    id("info: ${wrapper.ownerId}")
                    owner(if(!wrapper.ownerNickname.isNullOrEmpty()) wrapper.ownerNickname else wrapper.ownerId)
                    onEditClicked { _, _, _, _ -> queueInterface.onChangeNicknameClicked(wrapper.ownerId, wrapper.ownerNickname) }
                }
            }
        }
    }
}