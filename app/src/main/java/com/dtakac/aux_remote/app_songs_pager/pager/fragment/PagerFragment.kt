package com.dtakac.aux_remote.app_songs_pager.pager.fragment

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.app_songs_pager.pager.wrapper.UserQueuedSongUi
import com.dtakac.aux_remote.base.fragment.BaseFragment
import com.dtakac.aux_remote.base.fragment.newFragmentInstance
import com.dtakac.aux_remote.app_songs_pager.all_songs.fragment.AllSongsFragment
import com.dtakac.aux_remote.app_songs_pager.queue.fragment.QueueFragment
import com.dtakac.aux_remote.app_songs_pager.view_model.SongsPagerViewModel
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.appcompat.queryTextChanges
import kotlinx.android.synthetic.main.fragment_pager.*
import org.apache.commons.lang3.StringUtils
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit

private const val TAG = "pager_tag"
class PagerFragment: BaseFragment(){

    override val layoutRes: Int = R.layout.fragment_pager
    private val viewModel by viewModel<SongsPagerViewModel>()

    override fun initViews() {
        super.initViews()
        initToolbar()
        initPager()

        viewModel.apply {
            getAllSongs().subscribeByAndDispose()
            getQueuedSongs().subscribeByAndDispose()
            getNowPlayingSong().subscribeByAndDispose(
                onNext = { if(it.isUserSong) showViewQueueSnackbar(getString(R.string.nowplaying_snackbar)) }
            )
            pullFromServer()
        }
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
        viewModel.userQueuedSongLiveData.observe(this, Observer<UserQueuedSongUi>{
            showViewQueueSnackbar(getString(it.snackbarMessageId))
        })
    }

    private fun showViewQueueSnackbar(message: String){
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
            .skip(1) // because text change fires when view is created -_-
            .filter { it.isEmpty() || !it.isBlank() } // accepts empty input, but not whitespaces
            .subscribeByAndDispose {
                Log.d(TAG, "search changed: $it")
                viewModel.onQueryTextChanged(it.toString())
        }
    }

    private fun initPager(){
        // attach adapter to pager
        pager.adapter = PagerAdapter(
            resources.getStringArray(R.array.labels_fragments),
            this
        )
        // setup tablayout with pager
        tabLayout.setupWithViewPager(pager)
    }
}

class PagerAdapter(private val titles: Array<String>, f: Fragment): FragmentStatePagerAdapter(f.childFragmentManager){
    override fun getCount(): Int = 2
    override fun getItem(position: Int): Fragment =
        when(position){
            0 -> newFragmentInstance<AllSongsFragment>(Bundle.EMPTY)
            1 -> newFragmentInstance<QueueFragment>(Bundle.EMPTY)
            else -> throw IllegalStateException("No fragment defined for position: $position")
        }

    override fun getPageTitle(position: Int): CharSequence? = titles[position]
}