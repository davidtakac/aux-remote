package com.dtakac.aux_remote.connect.presenter

import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.prefs.SharedPrefsRepository
import com.dtakac.aux_remote.common.base.resource_repo.ResourceRepository
import com.dtakac.aux_remote.common.constants.CLIENT_MAC
import com.dtakac.aux_remote.common.constants.PREFS_IP_INPUT
import com.dtakac.aux_remote.common.constants.PREFS_PORT_INPUT
import com.dtakac.aux_remote.common.constants.PREFS_USER_ID
import com.dtakac.aux_remote.common.database.AppDatabase
import com.dtakac.aux_remote.common.network.NetworkUtil
import com.dtakac.aux_remote.common.network.ClientSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.*

class ConnectPresenter(
    private val view: ConnectContract.View,
    private val prefsRepo: SharedPrefsRepository,
    private val resourceRepo: ResourceRepository,
    private val netUtil: NetworkUtil,
    private val client: ClientSocket,
    private val db: AppDatabase
) : ConnectContract.Presenter {

    override fun onViewCreated() {
        closeClientSocket()
        clearDatabase()
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
            view.setIpAddressError(resourceRepo.getString(R.string.error_ipaddr_invalid))
        }
        if(!netUtil.isValidPort(port)){
            inputError = true
            view.setPortError(resourceRepo.getString(R.string.error_portnum_invalid))
        }
        if(!netUtil.isDeviceConnectedToWifi()){
            connectionError = true
            view.showWifiNeededMessage()
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
        view.setIpAddress(prefsRepo.get(PREFS_IP_INPUT, ""))
        view.setPort(prefsRepo.get(PREFS_PORT_INPUT, ""))
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
        val writer = BufferedWriter(OutputStreamWriter(client.outputStream ?: return, StandardCharsets.UTF_8))
        writer.apply{
            write(CLIENT_MAC); newLine()
            write(prefsRepo.get(PREFS_USER_ID, "")); newLine()
            flush()
        }
    }

    private fun saveInputToPrefs(ipAddress: String, port: String){
        prefsRepo.save(PREFS_IP_INPUT, ipAddress)
        prefsRepo.save(PREFS_PORT_INPUT, port)
    }

    private fun clearDatabase(){
        CoroutineScope(IO).launch { db.clearAllTables() }
    }

    private fun closeClientSocket(){
        CoroutineScope(IO).launch { client.close() }
    }
}