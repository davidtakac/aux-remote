package com.dtakac.aux_remote.server

import android.util.Log
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.InetAddress
import java.net.Socket

const val TAG = "server_socket"
class ServerSocket{
    private var socket: Socket? = null

    var outputStream: OutputStream? = null
        private set
        get() = socket?.getOutputStream()

    var inputStream: InputStream? = null
        private set
        get() = socket?.getInputStream()

    fun initialize(ipAddress: String, port: Int): Boolean{
        close()
        try {
            socket = Socket(InetAddress.getByName(ipAddress), port)
        } catch(e: Exception){
            Log.e(TAG, "Server socket initialization failed for IP Address: $ipAddress and port number: $port.")
            return false
        }
        return true
    }

    fun close(): Boolean{
        if(socket == null) return false

        try {
            socket?.close()
        } catch (e: Exception){
            Log.e(TAG, "Server socket couldn't close due to: ${e.message}")
            return false
        }

        socket = null
        return true
    }
}