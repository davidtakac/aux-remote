package com.dtakac.aux_remote.connect

interface ConnectContract {
    interface View{
        fun setIpAddress(ipAddress: String)
        fun setPort(port: String)
        fun getIpAddress(): String
        fun getPort(): String
        fun setIpAddressError(isError: Boolean)
        fun setPortError(isError: Boolean)
        fun showWifiNeededSnackbar()
    }

    interface Presenter{
        fun onViewCreated()
        fun onConnectClicked()
    }
}