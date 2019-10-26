package com.dtakac.aux_remote.connect

import com.dtakac.aux_remote.base.SharedPrefsRepo
import com.dtakac.aux_remote.common.PREFS_IP_INPUT
import com.dtakac.aux_remote.common.PREFS_PORT_INPUT
import com.dtakac.aux_remote.common.PREFS_USER_ID
import com.dtakac.aux_remote.network.NetworkUtil
import java.util.*

class ConnectPresenter(
    private val view: ConnectContract.View,
    private val prefsRepo: SharedPrefsRepo,
    private val netUtil: NetworkUtil
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
        if(!netUtil.isValidLocalIpAddress(view.getIpAddress())){
            inputError = true
            view.setIpAddressError(true)
        }
        if(!netUtil.isValidPort(view.getPort())){
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
            //todo: initialize socket with given ip and port
        }
    }

    private fun initInputFields(){
        view.apply {
            setIpAddress(prefsRepo.get(PREFS_IP_INPUT, ""))
            setPort(prefsRepo.get(PREFS_PORT_INPUT, ""))
        }
    }
}