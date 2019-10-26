package com.dtakac.aux_remote.common

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.dtakac.aux_remote.base.Router
import com.dtakac.aux_remote.base.displayFragment
import com.dtakac.aux_remote.common.FRAGMENT_CONNECT
import com.dtakac.aux_remote.common.FRAGMENT_PAGER
import com.dtakac.aux_remote.connect.ConnectFragment

class AuxRouter : Router{

    override fun showFragment(
        fragmentManager: FragmentManager?,
        bundle: Bundle,
        tag: String,
        container: Int
    ) {
        when(tag){
            FRAGMENT_CONNECT -> displayFragment<ConnectFragment>(fragmentManager, bundle, tag, container)
            FRAGMENT_PAGER -> TODO()
        }
    }

    override fun popBackStack(fragmentManager: FragmentManager?) {
        fragmentManager?.popBackStack()
    }
}