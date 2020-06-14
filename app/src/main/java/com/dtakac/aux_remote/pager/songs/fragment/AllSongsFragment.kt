package com.dtakac.aux_remote.pager.songs.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import com.airbnb.epoxy.TypedEpoxyController
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.fragment.BaseFragment
import com.dtakac.aux_remote.pager.view_model.SongsPagerViewModel
import com.dtakac.aux_remote.pager.songs.view_holders.song
import com.dtakac.aux_remote.pager.songs.wrapper.SongWrapper
import kotlinx.android.synthetic.main.fragment_songs.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class AllSongsFragment : BaseFragment(),
    AllSongsInterface {

    override val layoutRes = R.layout.fragment_songs
    private val controller by inject<AllSongsController>{ parametersOf(this)}
    private val viewModel by sharedViewModel<SongsPagerViewModel>(from = {parentFragment!!})

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.songsLiveData.observe(this, Observer<List<SongWrapper>>{
            controller.setData(it)
        })
        viewModel.filteredSongsLiveData.observe(this, Observer<List<SongWrapper>>{
            controller.setData(it)
        })
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

interface AllSongsInterface{
    fun onSongClicked(name: String)
}

class AllSongsController(private val allSongsInterface: AllSongsInterface): TypedEpoxyController<List<SongWrapper>>(){
    override fun buildModels(data: List<SongWrapper>) {
        data.forEach {
            song {
                id(it.id)
                name(it.highlightedName)
                clickListener { _, _, _, _ -> allSongsInterface.onSongClicked(it.name) }
            }
        }
    }
}