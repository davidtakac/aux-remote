package com.dtakac.aux_remote.connect.presenter

interface ConnectContract {
    interface View{
        fun setIpAddress(ipAddress: String)
        fun setPort(port: String)
        fun getIpAddress(): String
        fun getPort(): String
        fun setIpAddressError(msg: String?)
        fun setPortError(msg: String?)
        fun showWifiNeededMessage()
        fun onSocketInitialized()
        fun showLongSnackbar(text: String)
        fun showLoading(isLoading: Boolean)
        fun connectEnabled(isEnabled: Boolean)
        fun showMessage()
    }

    interface Presenter{
        fun onViewCreated()
        fun onConnectClicked()
    }
}