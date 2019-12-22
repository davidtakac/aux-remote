package com.dtakac.aux_remote.activity

import android.os.Bundle
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.fragment.newFragmentInstance
import com.dtakac.aux_remote.app_connect.fragment.ConnectFragment
import com.dtakac.aux_remote.base.activity.BaseActivity

class MainActivity : BaseActivity() {
    override val layoutRes = R.layout.activity_main

    override fun onCreated() {
        displayConnectFragment()
    }

    private fun displayConnectFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame,
                newFragmentInstance<ConnectFragment>(Bundle.EMPTY)
            )
            .commit()
    }
}
