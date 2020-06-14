package com.dtakac.aux_remote.connect.presenter

interface ConnectContract {
    interface View{
        fun setIpAddress(ipAddress: String)
        fun setPort(port: String)
        fun getIpAddress(): String
        fun getPort(): String
        fun setIpAddressError(isError: Boolean)
        fun setPortError(isError: Boolean)
        fun showWifiNeededSnackbar()
        fun onSocketInitialized()
        fun showLongSnackbar(stringId: Int)
        fun showLoading(isLoading: Boolean)
        fun connectEnabled(isEnabled: Boolean)
    }

    interface Presenter{
        fun onViewCreated()
        fun onConnectClicked()
    }
}