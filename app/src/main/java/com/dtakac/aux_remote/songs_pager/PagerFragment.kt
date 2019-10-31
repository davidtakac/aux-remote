package com.dtakac.aux_remote.songs_pager

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.BaseFragment
import com.dtakac.aux_remote.base.newFragmentInstance
import com.dtakac.aux_remote.songs_pager.all_songs.AllSongsFragment
import com.dtakac.aux_remote.songs_pager.queue.QueueFragment
import kotlinx.android.synthetic.main.fragment_pager.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.IllegalStateException

private const val TAG = "pager_tag"
class PagerFragment: BaseFragment(){
    override val layoutRes: Int = R.layout.fragment_pager
    private val viewModel by viewModel<SongsPagerViewModel>()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        initSearchView(menu.findItem(R.id.menu_search))
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun initViews() {
        super.initViews()
        setHasOptionsMenu(true)
        initPager()
    }

    private fun initSearchView(item: MenuItem){
        item.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                Log.d(TAG, "search expanded")
                pager.setCurrentItem(0, true)
                viewModel.onSearchViewExpanded()
                return true // needed so the view expands
            }
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                Log.d(TAG, "search collapsed")
                viewModel.onSearchViewCollapsed()
                return true // needed so the view collapses
            }
        })
        val search = item.actionView as SearchView
        search.queryHint = getString(R.string.hint_search_songs)
        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d(TAG, "search submit: $query")
                return true // because we're handling the submit (otherwise activity is started)
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG, "search changed: $newText")
                viewModel.onQueryTextChanged(newText ?: return true)
                return true // because we're handling the text changes
            }
        })
    }

    private fun initPager(){
        // attach adapter to pager
        pager.adapter = PagerAdapter(resources.getStringArray(R.array.labels_fragments), this)
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