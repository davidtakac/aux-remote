package com.dtakac.aux_remote.connect

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.BaseFragment
import com.dtakac.aux_remote.common.FRAGMENT_PAGER
import com.dtakac.aux_remote.common.defaultSchedulers
import com.dtakac.aux_remote.service.ResponseHandlerService
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.widget.textChanges
import kotlinx.android.synthetic.main.fragment_connect.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ConnectFragment : BaseFragment(), ConnectContract.View{
    override val layoutRes: Int = R.layout.fragment_connect
    private val presenter by inject<ConnectContract.Presenter>{ parametersOf(this) }
    private val TAG = "connecttag"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        presenter.onViewCreated()
    }

    override fun initViews(){
        super.initViews()
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

    override fun onSocketInitialized() {
        ResponseHandlerService.start(activity!!)
        router.showFragment(fragmentManager, Bundle.EMPTY, FRAGMENT_PAGER)
    }

    override fun showLongSnackbar(stringId: Int) {
        Snackbar.make(activity!!.findViewById(android.R.id.content), getString(stringId), Snackbar.LENGTH_LONG)
            .show()
    }

    override fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
    }

    override fun connectEnabled(isEnabled: Boolean) {
        btnConnect.isEnabled = isEnabled
    }
}