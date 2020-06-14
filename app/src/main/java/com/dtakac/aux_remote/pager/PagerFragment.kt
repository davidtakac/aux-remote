package com.dtakac.aux_remote.pager

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.fragment.BaseFragment
import com.dtakac.aux_remote.pager.adapter.PagerAdapter
import com.dtakac.aux_remote.pager.common.FeedbackMessage
import com.dtakac.aux_remote.pager.view_model.SongsPagerViewModel
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.appcompat.queryTextChanges
import kotlinx.android.synthetic.main.fragment_pager.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

private const val TAG = "pager_tag"
class PagerFragment: BaseFragment(){
    companion object {
        const val SONGS_VIEW_POSITION = 0
        const val QUEUE_VIEW_POSITION = 1
    }
    override val layoutRes: Int = R.layout.fragment_pager
    private val viewModel by viewModel<SongsPagerViewModel>()

    override fun initViews() {
        super.initViews()
        initToolbar()
        initPager()
    }

    private fun initToolbar(){
        toolbar.inflateMenu(R.menu.menu)
        initSearchView(toolbar.menu.findItem(R.id.menu_search))
        toolbar.setNavigationOnClickListener { fragmentManager?.popBackStack() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.feedbackMessage.observe(viewLifecycleOwner, Observer { showFeedbackMessage(it) })
        viewModel.pullFromServer()
    }

    private fun showFeedbackMessage(feedbackMessage: FeedbackMessage?){
        if(feedbackMessage == null) return
        val snackbar = Snackbar.make(
            activity!!.findViewById(android.R.id.content),
            feedbackMessage.message,
            Snackbar.LENGTH_LONG
        )
        if(feedbackMessage.action != null && pager.currentItem != QUEUE_VIEW_POSITION) {
            snackbar.setAction(feedbackMessage.action) { pager.currentItem = QUEUE_VIEW_POSITION }
        }
        snackbar.show()
    }

    private fun initSearchView(item: MenuItem){
        val search = item.actionView as SearchView
        search.queryHint = getString(R.string.hint_search_songs)

        item.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                Log.d(TAG, "search expanded")
                pager.setCurrentItem(SONGS_VIEW_POSITION, true)
                return true // needed so the view expands
            }
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                Log.d(TAG, "search collapsed")
                viewModel.onSearchCollapsed()
                return true // needed so the view collapses
            }
        })

        pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                if(position != SONGS_VIEW_POSITION){
                    item.collapseActionView()
                }
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
        pager.adapter = PagerAdapter(
            resources.getStringArray(R.array.labels_fragments),
            this
        )
        tabLayout.setupWithViewPager(pager)
    }
}