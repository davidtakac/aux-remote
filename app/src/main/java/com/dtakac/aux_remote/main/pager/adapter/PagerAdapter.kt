package com.dtakac.aux_remote.main.pager.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.dtakac.aux_remote.common.base.fragment.newFragmentInstance
import com.dtakac.aux_remote.main.pager.fragment.PagerFragment
import com.dtakac.aux_remote.main.queue.fragment.QueueFragment
import com.dtakac.aux_remote.main.songs.fragment.SongsListFragment
import java.lang.IllegalStateException

class PagerAdapter(private val titles: Array<String>, f: Fragment): FragmentStatePagerAdapter(f.childFragmentManager){
    override fun getCount(): Int = 2
    override fun getItem(position: Int): Fragment =
        when(position){
            PagerFragment.SONGS_VIEW_POSITION -> newFragmentInstance<SongsListFragment>(Bundle.EMPTY)
            PagerFragment.QUEUE_VIEW_POSITION -> newFragmentInstance<QueueFragment>(Bundle.EMPTY)
            else -> throw IllegalStateException("No fragment defined for position: $position")
        }
    override fun getPageTitle(position: Int): CharSequence? = titles[position]
}