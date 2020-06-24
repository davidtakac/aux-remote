package com.dtakac.aux_remote.main.queue.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.fragment.BaseFragment
import com.dtakac.aux_remote.main.pager.fragment.PagerFragmentDirections
import com.dtakac.aux_remote.main.queue.controller.QueueController
import com.dtakac.aux_remote.main.queue.controller.QueueInterface
import com.dtakac.aux_remote.main.view_model.SongsPagerViewModel
import kotlinx.android.synthetic.main.fragment_queue.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class QueueFragment : BaseFragment(), QueueInterface{
    override val layoutRes = R.layout.fragment_queue
    private val controller by inject<QueueController>{ parametersOf(this) }
    private val viewModel by sharedViewModel<SongsPagerViewModel>(from = { requireParentFragment() })

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.queue.observe(viewLifecycleOwner, Observer{ controller.setQueue(it) })
        viewModel.nowPlayingSong.observe(viewLifecycleOwner, Observer { controller.setNowPlayingSong(it) })
        viewModel.queueLoader.observe(viewLifecycleOwner, Observer { progressIndicator.visibility = it })
    }

    override fun initViews() {
        super.initViews()
        rvQueue.setController(controller)
    }

    override fun onChangeNicknameClicked(ownerId: String, currentNickname: String?) {
        findNavController().navigate(PagerFragmentDirections.showNicknameDialog(ownerId, currentNickname))
    }
}