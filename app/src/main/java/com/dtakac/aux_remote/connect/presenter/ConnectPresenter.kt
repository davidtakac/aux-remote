package com.dtakac.aux_remote.connect.presenter

import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.resource.ResourceRepository
import com.dtakac.aux_remote.common.prefs.AuxSharedPrefsRepository
import com.dtakac.aux_remote.common.util.NetworkUtil
import com.dtakac.aux_remote.common.repository.Repository
import com.dtakac.aux_remote.server.ServerInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ConnectPresenter(
    private val view: ConnectContract.View,
    private val prefsRepo: AuxSharedPrefsRepository,
    private val resourceRepo: ResourceRepository,
    private val netUtil: NetworkUtil,
    private val serverInteractor: ServerInteractor,
    private val repo: Repository
) : ConnectContract.Presenter {

    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    override fun onViewCreated() {
        closeSocket()
        if(prefsRepo.getUserId().isBlank()){
            prefsRepo.saveUserId(UUID.randomUUID().toString())
        }
        initInputFields()
        view.showMessage()
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
        view.setIpAddress(prefsRepo.getIpAddress())
        view.setPort(prefsRepo.getPortNumber())
    }

    private fun initializeSocket(ipAddress: String, port: String){
        scope.launch {
            val success = serverInteractor.initializeSocket(ipAddress, port)
            if(success) {
                clearDatabase()
                saveInputToPrefs(ipAddress, port)
                connectToServer()
            }
            // UI related behavior that needs to be performed on main thread
            withContext(Dispatchers.Main){
                view.showLoading(false)
                view.connectEnabled(true)
                if(success) {
                    view.onSocketInitialized()
                }
                else {
                    view.showLongSnackbar(resourceRepo.getString(R.string.error_cantconnect))
                }
            }
        }
    }

    private suspend fun connectToServer(){
        serverInteractor.initializeReaderAndWriter()
        serverInteractor.connectToServer(prefsRepo.getUserId())
    }

    private fun saveInputToPrefs(ipAddress: String, port: String){
        prefsRepo.saveIpAddress(ipAddress)
        prefsRepo.savePortNumber(port)
    }

    private fun closeSocket(){
        scope.launch { serverInteractor.closeSocket() }
    }

    private fun clearDatabase(){
        scope.launch { repo.clearData() }
    }
}