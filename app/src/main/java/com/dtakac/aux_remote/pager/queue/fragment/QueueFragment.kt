package com.dtakac.aux_remote.pager.queue.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import com.airbnb.epoxy.TypedEpoxyController
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.fragment.BaseFragment
import com.dtakac.aux_remote.pager.view_model.SongsPagerViewModel
import com.dtakac.aux_remote.pager.queue.view_holders.nowPlayingSong
import com.dtakac.aux_remote.pager.queue.view_holders.queuedSong
import com.dtakac.aux_remote.pager.queue.wrapper.QueueUi
import kotlinx.android.synthetic.main.fragment_queue.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

class QueueFragment : BaseFragment(){
    override val layoutRes = R.layout.fragment_queue
    private val controller by inject<QueueController>()
    private val viewModel by sharedViewModel<SongsPagerViewModel>(from = {parentFragment!!})

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.queueLiveData.observe(this, Observer<QueueUi>{
            controller.setData(it)
        })
    }

    override fun initViews() {
        super.initViews()
        rvQueue.setController(controller)
    }
}

class QueueController: TypedEpoxyController<QueueUi>(){
    override fun buildModels(data: QueueUi) {
        nowPlayingSong {
            id("now-playing")
            name(data.nowPlayingSong.name)
        }
        data.queuedSongs.forEach { queuedSong ->
            queuedSong {
                id(queuedSong.ownerId)
                position(queuedSong.position.toString())
                name(queuedSong.name)
                userIconVisibility(queuedSong.userIconVisibility)
            }
        }
    }
}