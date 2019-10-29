package com.dtakac.aux_remote.songs_pager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.BaseFragment
import com.dtakac.aux_remote.base.newFragmentInstance
import com.dtakac.aux_remote.songs_pager.all_songs.AllSongsFragment
import com.dtakac.aux_remote.songs_pager.queue.QueueFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_connect.*
import kotlinx.android.synthetic.main.fragment_pager.*
import kotlinx.android.synthetic.main.fragment_pager.toolbar
import java.lang.IllegalStateException

class PagerFragment: BaseFragment(){
    override val layoutRes: Int = R.layout.fragment_pager

    override fun initViews() {
        super.initViews()
        initPager()
    }

    private fun initPager(){
        // attach adapter to pager
        pager.adapter = PagerAdapter(this)
        // setup tablayout with pager
        TabLayoutMediator(tabLayout, pager, false,
            TabLayoutMediator.OnConfigureTabCallback { tab, position ->
                pager.setCurrentItem(tab.position, true)
                tab.text = resources.getStringArray(R.array.labels_fragments)[position]
            }
        ).attach()
        // setup toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.label_pager_fragment)
    }
}

class PagerAdapter(f: Fragment): FragmentStateAdapter(f){
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment =
        when(position){
            0 -> newFragmentInstance<AllSongsFragment>(Bundle.EMPTY)
            1 -> newFragmentInstance<QueueFragment>(Bundle.EMPTY)
            else -> throw IllegalStateException("No fragment defined for position: $position")
        }
}