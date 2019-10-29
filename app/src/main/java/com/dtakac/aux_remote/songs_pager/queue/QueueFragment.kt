package com.dtakac.aux_remote.songs_pager.queue

import android.os.Bundle
import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.BaseFragment
import com.dtakac.aux_remote.songs_pager.queue.view_holders.nowPlayingSong
import com.dtakac.aux_remote.songs_pager.queue.view_holders.queuedSong
import kotlinx.android.synthetic.main.fragment_queue.*
import org.koin.android.ext.android.inject

class QueueFragment : BaseFragment(){
    override val layoutRes = R.layout.fragment_queue
    private val controller by inject<QueueController>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //todo: sub to queue live data and update controller on change
    }

    override fun initViews() {
        super.initViews()
        rvQueue.setController(controller)
        testRecycler()
    }

    private fun testRecycler(){
        val nowPlayingSong = "Moj je dado puno radio"
        val queue = listOf(
            QueuedSong("Death Grips - Pss Pss", View.VISIBLE),
            QueuedSong("Death Grips - Anne Bonny"),
            QueuedSong("Brand New - Daisy"),
            QueuedSong("A ne znam vise ne da mi se")
        )
        controller.setData(QueueUi(nowPlayingSong, queue))
    }
}

class QueueController: TypedEpoxyController<QueueUi>(){
    override fun buildModels(data: QueueUi) {
        nowPlayingSong {
            id("now-playing")
            name(data.nowPlayingSongName)
        }
        data.queuedSongs.forEachIndexed { idx, queuedSong ->
            queuedSong {
                id("$idx-${queuedSong.songName}")
                position("${idx+1}")
                name(queuedSong.songName)
                userIconVisibility(queuedSong.userIconVisibility)
            }
        }
    }
}