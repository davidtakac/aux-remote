package com.dtakac.aux_remote.connect

import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.SharedPrefsRepo
import com.dtakac.aux_remote.common.CLIENT_MAC
import com.dtakac.aux_remote.common.PREFS_IP_INPUT
import com.dtakac.aux_remote.common.PREFS_PORT_INPUT
import com.dtakac.aux_remote.common.PREFS_USER_ID
import com.dtakac.aux_remote.network.NetworkUtil
import com.dtakac.aux_remote.network.ClientSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

class ConnectPresenter(
    private val view: ConnectContract.View,
    private val prefsRepo: SharedPrefsRepo,
    private val netUtil: NetworkUtil,
    private val client: ClientSocket
) : ConnectContract.Presenter{

    override fun onViewCreated() {
        if(prefsRepo.get(PREFS_USER_ID, "").isBlank()){
            prefsRepo.save(PREFS_USER_ID, UUID.randomUUID().toString())
        }
        initInputFields()
    }

    override fun onConnectClicked() {
        var inputError = false
        var connectionError = false
        val ipAddress = view.getIpAddress()
        val port = view.getPort()

        // validate input and set errors to corresponding UI elements
        if(!netUtil.isValidLocalIpAddress(ipAddress)){
            inputError = true
            view.setIpAddressError(true)
        }
        if(!netUtil.isValidPort(port)){
            inputError = true
            view.setPortError(true)
        }
        if(!netUtil.isDeviceConnectedToWifi()){
            connectionError = true
            view.showWifiNeededSnackbar()
        }

        if(inputError || connectionError){
            return
        } else {
            view.showLoading(true)
            view.connectEnabled(false)
            initializeSocket(ipAddress, port)
        }
    }

    private fun initInputFields(){
        view.apply {
            setIpAddress(prefsRepo.get(PREFS_IP_INPUT, ""))
            setPort(prefsRepo.get(PREFS_PORT_INPUT, ""))
        }
    }

    private fun initializeSocket(ipAddress: String, port: String){
        CoroutineScope(IO).launch {
            val success = client.initialize(ipAddress, Integer.parseInt(port))
            // operations to be performed while still on background thread
            if(success) {
                saveInputToPrefs(ipAddress, port)
                connectToServer()
            }
            // UI related behavior that needs to be performed on main thread
            withContext(Main){
                view.showLoading(false)
                view.connectEnabled(true)
                if(success) view.onSocketInitialized()
                else view.showLongSnackbar(R.string.error_cantconnect)
            }
        }
    }

    private fun connectToServer(){
        // socket is definitely initialized when this method is called
        val w = BufferedWriter(OutputStreamWriter(client.outputStream!!, StandardCharsets.UTF_8))
        w.write(CLIENT_MAC)
        w.newLine()
        w.write(prefsRepo.get(PREFS_USER_ID, ""))
        w.newLine()
        w.flush()
    }

    private fun saveInputToPrefs(ipAddress: String, port: String){
        prefsRepo.save(PREFS_IP_INPUT, ipAddress)
        prefsRepo.save(PREFS_PORT_INPUT, port)
    }
}