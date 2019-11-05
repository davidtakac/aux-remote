package com.dtakac.aux_remote.songs_pager.all_songs

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.airbnb.epoxy.TypedEpoxyController
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.BaseFragment
import com.dtakac.aux_remote.common.defaultSchedulers
import com.dtakac.aux_remote.data.song.Song
import com.dtakac.aux_remote.songs_pager.SongsPagerViewModel
import com.dtakac.aux_remote.songs_pager.all_songs.view_holders.song
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_songs.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

private const val TAG = "all_songs_tag"
class AllSongsFragment : BaseFragment(), AllSongsInterface{

    override val layoutRes = R.layout.fragment_songs
    private val controller by inject<AllSongsController>{ parametersOf(this)}
    private val viewModel by sharedViewModel<SongsPagerViewModel>(from = {parentFragment!!})

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.songsLiveData.observe(this, Observer<AllSongsUi>{
            if(it.isSearching){
                controller.setData(it.filteredSongs)
            } else {
                controller.setData(it.songs)
            }
        })
    }

    override fun onSongClicked(id: Int) {
        viewModel.onSongClicked(id)
    }

    override fun initViews() {
        super.initViews()
        rvSongs.setController(controller)
    }
}

interface AllSongsInterface{
    fun onSongClicked(id: Int)
}

class AllSongsController(private val allSongsInterface: AllSongsInterface): TypedEpoxyController<List<Song>>(){
    override fun buildModels(data: List<Song>) {
        data.forEach{ song ->
            song {
                id("${song.id}")
                name(song.name)
                clickListener { _, _, _, _ ->
                    allSongsInterface.onSongClicked(song.id!!)
                }
            }
        }
    }
}