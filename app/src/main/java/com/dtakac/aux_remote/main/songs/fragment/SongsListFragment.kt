package com.dtakac.aux_remote.main.songs.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.fragment.BaseFragment
import com.dtakac.aux_remote.main.songs.controller.SongsListController
import com.dtakac.aux_remote.main.songs.controller.SongsListInterface
import com.dtakac.aux_remote.main.view_model.SongsPagerViewModel
import kotlinx.android.synthetic.main.fragment_songs_list.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class SongsListFragment : BaseFragment(), SongsListInterface {
    override val layoutRes = R.layout.fragment_songs_list
    private val controller by inject<SongsListController>{ parametersOf(this)}
    private val viewModel by sharedViewModel<SongsPagerViewModel>(from = { requireParentFragment() })

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.songs.observe(viewLifecycleOwner, Observer { controller.setSongs(it) })
        viewModel.filteredSongs.observe(viewLifecycleOwner, Observer { controller.setFilteredSongs(it) })
        viewModel.songsMode.observe(viewLifecycleOwner, Observer { controller.setMode(it) })
    }

    override fun onSongClicked(name: String) {
        viewModel.onSongClicked(name)
    }

    override fun initViews() {
        super.initViews()
        initRecycler()
    }

    private fun initRecycler() {
        rvSongs.setController(controller)
    }
}