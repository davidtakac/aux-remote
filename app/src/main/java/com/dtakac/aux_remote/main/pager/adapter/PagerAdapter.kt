package com.dtakac.aux_remote.main.pager.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dtakac.aux_remote.common.base.fragment.newFragmentInstance
import com.dtakac.aux_remote.main.pager.fragment.PagerFragment
import com.dtakac.aux_remote.main.queue.fragment.QueueFragment
import com.dtakac.aux_remote.main.songs.fragment.SongsListFragment
import java.lang.IllegalStateException

class PagerAdapter(f: Fragment): FragmentStateAdapter(f){
    override fun getItemCount(): Int {
        return 2
    }
    override fun createFragment(position: Int): Fragment {
        return when(position){
            PagerFragment.SONGS_VIEW_POSITION -> newFragmentInstance<SongsListFragment>(Bundle.EMPTY)
            PagerFragment.QUEUE_VIEW_POSITION -> newFragmentInstance<QueueFragment>(Bundle.EMPTY)
            else -> throw IllegalStateException("No fragment defined for position: $position")
        }
    }
}