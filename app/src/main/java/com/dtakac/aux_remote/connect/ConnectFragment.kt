package com.dtakac.aux_remote.connect

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.BaseFragment
import com.dtakac.aux_remote.common.defaultSchedulers
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.widget.textChanges
import kotlinx.android.synthetic.main.fragment_connect.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ConnectFragment : BaseFragment(), ConnectContract.View{
    override val layoutRes: Int = R.layout.fragment_connect
    private val presenter by inject<ConnectContract.Presenter>{ parametersOf(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews(){
        btnConnect.setOnClickListener { presenter.onConnectClicked() }
        addDisposable(etIpAddress.textChanges().defaultSchedulers().subscribe{
            setIpAddressError(false)
        })
        addDisposable(etPort.textChanges().defaultSchedulers().subscribe{
            setPortError(false)
        })
    }

    override fun setIpAddress(ipAddress: String) {
        etIpAddress.setText(ipAddress)
    }

    override fun setPort(port: String) {
        etPort.setText(port)
    }

    override fun getIpAddress(): String = etIpAddress.text.toString()

    override fun getPort(): String = etPort.text.toString()

    override fun setIpAddressError(isError: Boolean) {
        tilIpAddress.error = if (isError) getString(R.string.error_ipaddr_invalid) else null
    }

    override fun setPortError(isError: Boolean) {
        tilPort.error = if (isError) getString(R.string.error_portnum_invalid) else null
    }

    override fun showWifiNeededSnackbar() {
        Snackbar.make(activity!!.findViewById(android.R.id.content), getString(R.string.error_internet_needwifi), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.snackbar_action_wifi_settings)) {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
            .show()
    }
}