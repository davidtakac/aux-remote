package com.dtakac.aux_remote.songs_pager.all_songs

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import com.airbnb.epoxy.TypedEpoxyController
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.BaseFragment
import com.dtakac.aux_remote.songs_pager.all_songs.view_holders.song
import kotlinx.android.synthetic.main.fragment_songs.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

private const val TAG = "all_songs_tag"
class AllSongsFragment : BaseFragment(), AllSongsInterface{
    override val layoutRes = R.layout.fragment_songs
    private val controller by inject<AllSongsController>{ parametersOf(this)}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //todo: viewModel subscribe to allSongsData and update controller on change
    }

    override fun onSongClicked(position: Int) {
        Log.d(TAG, "Clicked song on pos: $position")
        // todo: notify viewmodel which sends clicked song to server
    }

    override fun initViews() {
        super.initViews()
        rvSongs.setController(controller)
        testRecycler()
    }

    //todo: make this work with one activity multiple fragment architecture
    private fun initSearchView(item: MenuItem){
        item.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                //todo: viewModel.onSearchExpanded()

                // return true so the item expands
                return true
            }
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                //todo: viewModel.onSearchCollapsed

                // return true so it collapses
                return true
            }
        })
        val search = item.actionView as SearchView
        search.queryHint = getString(R.string.hint_search_songs)
        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                //returning true means we're handling the submit. otherwise a new search activity
                //would get started(default behavior)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //todo: viewModel.onQueryTextChanged(newText)
                return true
            }
        })
    }

    private fun testRecycler(){
        val songs = listOf("Vuco - Crna zeno",
            "OOF - Svaki dan ja oofujem",
            "Old town road",
            "IJAAAAAAH",
            "Oci boje kestena"
        )
        controller.setData(AllSongsUi(songs))
    }
}

interface AllSongsInterface{
    fun onSongClicked(position: Int)
}

class AllSongsController(private val allSongsInterface: AllSongsInterface): TypedEpoxyController<AllSongsUi>(){
    override fun buildModels(data: AllSongsUi) {
        data.songNames.forEachIndexed { idx, songName ->
            song {
                id("$idx-$songName")
                name(songName)
                clickListener { _, _, _, position ->
                    allSongsInterface.onSongClicked(position)
                }
            }
        }
    }
}