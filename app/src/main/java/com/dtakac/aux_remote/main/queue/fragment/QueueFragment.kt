package com.dtakac.aux_remote.main.queue.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.fragment.BaseFragment
import com.dtakac.aux_remote.main.queue.controller.QueueController
import com.dtakac.aux_remote.main.view_model.SongsPagerViewModel
import kotlinx.android.synthetic.main.fragment_queue.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

class QueueFragment : BaseFragment(){
    override val layoutRes = R.layout.fragment_queue
    private val controller by inject<QueueController>()
    private val viewModel by sharedViewModel<SongsPagerViewModel>(from = { requireParentFragment() })

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.queue.observe(viewLifecycleOwner, Observer{ controller.setQueue(it) })
        viewModel.nowPlayingSong.observe(viewLifecycleOwner, Observer { controller.setNowPlayingSong(it) })
    }

    override fun initViews() {
        super.initViews()
        rvQueue.setController(controller)
    }
}