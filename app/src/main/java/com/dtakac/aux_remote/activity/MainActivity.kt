package com.dtakac.aux_remote.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.Router
import com.dtakac.aux_remote.common.FRAGMENT_CONNECT
import com.dtakac.aux_remote.service.ResponseHandlerService
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val router by inject<Router>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        router.showFragment(supportFragmentManager, Bundle.EMPTY, FRAGMENT_CONNECT, R.id.frame)
        ResponseHandlerService.start(this)
    }
}
