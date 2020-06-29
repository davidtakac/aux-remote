package com.dtakac.aux_remote.connect.presenter

import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.resource.ResourceRepository
import com.dtakac.aux_remote.common.notification.NotificationHelper
import com.dtakac.aux_remote.common.prefs.AuxSharedPrefsRepository
import com.dtakac.aux_remote.common.util.NetworkUtil
import com.dtakac.aux_remote.common.repository.Repository
import com.dtakac.aux_remote.server.ServerInteractor
import kotlinx.coroutines.*
import java.util.*

class ConnectPresenter(
    private val view: ConnectContract.View,
    private val prefsRepo: AuxSharedPrefsRepository,
    private val resourceRepo: ResourceRepository,
    private val netUtil: NetworkUtil,
    private val serverInteractor: ServerInteractor,
    private val repo: Repository,
    private val notifs: NotificationHelper
) : ConnectContract.Presenter {

    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    override fun onViewCreated() {
        scope.launch { serverInteractor.closeConnection() }
        notifs.dismissNowPlayingSongNotification()
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
            view.setConnectEnabled(false)
            initializeSocket(ipAddress, port)
        }
    }

    private fun initInputFields(){
        view.setIpAddress(prefsRepo.getIpAddress())
        view.setPort(prefsRepo.getPortNumber())
    }

    private fun initializeSocket(ipAddress: String, port: String){
        scope.launch {
            val success = serverInteractor.initializeConnection(ipAddress, port)
            if(success) {
                //prepare for communication
                repo.clearPlayerSession()
                serverInteractor.initializeReaderAndWriter()
                serverInteractor.connect(prefsRepo.getUserId())
                //save correct input to prefs for future connections
                saveInputToPrefs(ipAddress, port)
            }
            //show results of operation
            view.showLoading(false)
            view.setConnectEnabled(true)
            if(success) {
                view.startMainScreen()
            }
            else {
                view.showLongSnackbar(resourceRepo.getString(R.string.error_cantconnect))
            }
        }
    }

    private fun saveInputToPrefs(ipAddress: String, port: String){
        prefsRepo.saveIpAddress(ipAddress)
        prefsRepo.savePortNumber(port)
    }
}