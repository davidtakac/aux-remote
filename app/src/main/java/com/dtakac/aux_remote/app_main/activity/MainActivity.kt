package com.dtakac.aux_remote.app_main.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.fragment.newFragmentInstance
import com.dtakac.aux_remote.app_connect.fragment.ConnectFragment
import com.dtakac.aux_remote.app_main.view_model.MainViewModel
import com.dtakac.aux_remote.base.activity.BaseActivity
import com.dtakac.aux_remote.common.constants.SERVICE_STOPPED_MESSAGE
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity() {
    override val layoutRes = R.layout.activity_main
    private val viewModel by inject<MainViewModel>()

    override fun onCreated() {
        super.onCreated()
        displayConnectFragment()
        viewModel.getMessage().subscribeByAndDispose()
        viewModel.messageLiveData.observe(this, Observer {
            if(it == SERVICE_STOPPED_MESSAGE) supportFragmentManager.popBackStack()
        })
    }

    private fun displayConnectFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame,
                newFragmentInstance<ConnectFragment>(Bundle.EMPTY)
            )
            .commit()
    }
}
