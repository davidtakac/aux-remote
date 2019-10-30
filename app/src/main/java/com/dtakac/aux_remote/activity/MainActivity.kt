package com.dtakac.aux_remote.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.newFragmentInstance
import com.dtakac.aux_remote.connect.ConnectFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        displayConnectFragment()
    }

    private fun initToolbar(){
        setSupportActionBar(toolbar)
    }

    private fun displayConnectFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, newFragmentInstance<ConnectFragment>(Bundle.EMPTY))
            .commit()
    }
}
