package com.dtakac.aux_remote.connect.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.navigation.fragment.findNavController
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.fragment.BaseFragment
import com.dtakac.aux_remote.connect.presenter.ConnectContract
import com.dtakac.aux_remote.service.ResponseHandlerService
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.widget.textChanges
import kotlinx.android.synthetic.main.fragment_connect.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ConnectFragment : BaseFragment(), ConnectContract.View {
    override val layoutRes: Int = R.layout.fragment_connect
    private val presenter by inject<ConnectContract.Presenter>{ parametersOf(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        presenter.onViewCreated()
    }

    override fun initViews(){
        super.initViews()
        btnConnect.setOnClickListener { presenter.onConnectClicked() }
        etIpAddress.textChanges().subscribeByAndDispose(
            onNext = { setIpAddressError(null) }
        )
        etPort.textChanges().subscribeByAndDispose(
            onNext = { setPortError(null) }
        )
    }

    override fun setIpAddress(ipAddress: String) {
        etIpAddress.setText(ipAddress)
    }

    override fun setPort(port: String) {
        etPort.setText(port)
    }

    override fun getIpAddress(): String = etIpAddress.text.toString()

    override fun getPort(): String = etPort.text.toString()

    override fun setIpAddressError(msg: String?) {
        tilIpAddress.error = msg
    }

    override fun setPortError(msg: String?) {
        tilPort.error = msg
    }

    override fun showWifiNeededMessage() {
        Snackbar.make(requireActivity().findViewById(android.R.id.content), getString(R.string.error_internet_needwifi), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.snackbar_action_wifi_settings)) {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
            .show()
    }

    override fun onSocketInitialized() {
        ResponseHandlerService.start(requireActivity())
        openPagerFragment()
    }

    override fun showLongSnackbar(stringId: Int) {
        Snackbar.make(requireActivity().findViewById(android.R.id.content), getString(stringId), Snackbar.LENGTH_LONG)
            .show()
    }

    override fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
    }

    override fun connectEnabled(isEnabled: Boolean) {
        btnConnect.isEnabled = isEnabled
    }

    private fun openPagerFragment(){
        val action = ConnectFragmentDirections.startPagerFragment()
        findNavController().navigate(action)
    }
}