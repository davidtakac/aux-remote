package com.dtakac.aux_remote.songs_pager

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.BaseFragment
import com.dtakac.aux_remote.base.newFragmentInstance
import com.dtakac.aux_remote.data.queued_song.QueuedSong
import com.dtakac.aux_remote.songs_pager.all_songs.AllSongsFragment
import com.dtakac.aux_remote.songs_pager.queue.QueueFragment
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.appcompat.queryTextChanges
import kotlinx.android.synthetic.main.fragment_pager.*
import org.apache.commons.lang3.StringUtils
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit

private const val TAG = "pager_tag"
private const val ABBR_MARKER = "…"
class PagerFragment: BaseFragment(){

    override val layoutRes: Int = R.layout.fragment_pager
    private val viewModel by viewModel<SongsPagerViewModel>()

    override fun initViews() {
        super.initViews()
        initToolbar()
        initPager()

        viewModel.getAllSongs().subscribeByAndDispose()
        /*viewModel.getQueuedSongs().subscribeByAndDispose()
        viewModel.getNowPlayingSong().subscribeByAndDispose(
            onNext = {
                if(it.isUserSong)
                    showQueueViewSnackbar(getString(R.string.nowplaying_snackbar)
                        .format(StringUtils.abbreviate(it.name, ABBR_MARKER, resources.getInteger(R.integer.playing_abbr_len)))
                    )
            }
        )*/
    }

    private fun initToolbar(){
        toolbar.inflateMenu(R.menu.menu)
        initSearchView(toolbar.menu.findItem(R.id.menu_search))
        toolbar.setNavigationOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.userQueuedSongLiveData.observe(this, Observer<QueuedSong>{
            showQueueViewSnackbar(getString(R.string.snackbar_queued_template)
                .format(
                    StringUtils.abbreviate(it.name, ABBR_MARKER, resources.getInteger(R.integer.queued_abbr_len)),
                    it.position + 1
                )
            )
        })
    }

    private fun showQueueViewSnackbar(message: String){
        Snackbar.make(activity!!.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .setAction(R.string.snackbar_action_view) { pager.currentItem = 1 }
            .show()
    }

    private fun initSearchView(item: MenuItem){
        val search = item.actionView as SearchView
        search.queryHint = getString(R.string.hint_search_songs)

        item.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                Log.d(TAG, "search expanded")
                pager.setCurrentItem(0, true)
                return true // needed so the view expands
            }
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                Log.d(TAG, "search collapsed")
                viewModel.onSearchCollapsed()
                return true // needed so the view collapses
            }
        })

        search.queryTextChanges()
            .debounce(300, TimeUnit.MILLISECONDS)
            .filter { it.isEmpty() || !it.isBlank() } // accepts empty input, but not whitespaces
            .subscribeByAndDispose {
                Log.d(TAG, "search changed: $it")
                viewModel.onQueryTextChanged(it.toString())
        }
    }

    private fun initPager(){
        // attach adapter to pager
        pager.adapter = PagerAdapter(resources.getStringArray(R.array.labels_fragments), this)
        // setup tablayout with pager
        tabLayout.setupWithViewPager(pager)
    }
}

class PagerAdapter(private val titles: Array<String>, f: Fragment): FragmentStatePagerAdapter(f.childFragmentManager){
    override fun getCount(): Int = 1
    override fun getItem(position: Int): Fragment =
        when(position){
            0 -> newFragmentInstance<AllSongsFragment>(Bundle.EMPTY)
            //1 -> newFragmentInstance<QueueFragment>(Bundle.EMPTY)
            else -> throw IllegalStateException("No fragment defined for position: $position")
        }

    override fun getPageTitle(position: Int): CharSequence? = titles[position]
}