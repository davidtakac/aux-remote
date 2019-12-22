package com.dtakac.aux_remote.activity

import android.os.Bundle
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.fragment.newFragmentInstance
import com.dtakac.aux_remote.app_connect.fragment.ConnectFragment
import com.dtakac.aux_remote.base.activity.BaseActivity
import com.dtakac.aux_remote.common.RelayMessage
import com.jakewharton.rxrelay2.PublishRelay
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity() {
    private val relay by inject<PublishRelay<RelayMessage>>()
    override val layoutRes = R.layout.activity_main

    override fun onCreated() {
        displayConnectFragment()
        relay.subscribeByAndDispose(
            onNext = {if(it == RelayMessage.SOCKET_EXCEPTION) supportFragmentManager.popBackStack()},
            onError = {}
        )
    }

    private fun displayConnectFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame,
                newFragmentInstance<ConnectFragment>(Bundle.EMPTY)
            )
            .commit()
    }
}
