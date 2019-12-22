package com.dtakac.aux_remote.common.network

import android.content.Context
import android.net.ConnectivityManager
import org.apache.commons.validator.routines.InetAddressValidator


class NetworkUtil(private val context: Context) {

    fun isValidLocalIpAddress(ipAddress: String): Boolean {
        return InetAddressValidator.getInstance().isValidInet4Address(ipAddress)
    }

    fun isValidPort(port: String?): Boolean {
        if (port == null || port.isEmpty()) return false

        val portNum: Int
        try {
            portNum = Integer.parseInt(port)
        } catch (e: NumberFormatException) {
            return false
        }

        //port numbers in range [0, 1023] are reserved.
        return portNum in 1024..65535
    }

    fun isDeviceOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun isDeviceConnectedToWifi(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        return isDeviceOnline() && activeNetwork!!.type == ConnectivityManager.TYPE_WIFI
    }
}