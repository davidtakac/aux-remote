package com.dtakac.aux_remote.songs_pager.queue

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.airbnb.epoxy.TypedEpoxyController
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.BaseFragment
import com.dtakac.aux_remote.common.defaultSchedulers
import com.dtakac.aux_remote.songs_pager.SongsPagerViewModel
import com.dtakac.aux_remote.songs_pager.queue.view_holders.nowPlayingSong
import com.dtakac.aux_remote.songs_pager.queue.view_holders.queuedSong
import io.reactivex.rxkotlin.subscribeBy
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

        addDisposable(viewModel.getQueuedSongs().defaultSchedulers().subscribeBy())

        addDisposable(viewModel.getNowPlayingSong().defaultSchedulers().subscribeBy(
            onNext = {
                if(it.isUserSong){
                    //todo: show snackbar that user song is playing
                }
            }
        ))
    }
}

class QueueController: TypedEpoxyController<QueueUi>(){
    override fun buildModels(data: QueueUi) {
        nowPlayingSong {
            id("now-playing")
            name(data.nowPlayingSong.name)
        }
        data.queuedSongs.forEachIndexed { idx, queuedSong ->
            queuedSong {
                id("${queuedSong.id}")
                position("${idx+1}")
                name(queuedSong.name)
                userIconVisibility(queuedSong.userIconVisibility)
            }
        }
    }
}