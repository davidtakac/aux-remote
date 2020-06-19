package com.dtakac.aux_remote.common.prefs

import com.dtakac.aux_remote.common.base.prefs.SharedPreferencesManager

// PREFS KEYS
private const val PREFS_USER_ID = "PREFS_USER_ID"
private const val PREFS_IP_INPUT = "PREFS_IP_INPUT"
private const val PREFS_PORT_INPUT = "PREFS_PORT_INPUT"

class AuxSharedPrefsRepositoryImpl(
    private val sharedPreferencesManager: SharedPreferencesManager
): AuxSharedPrefsRepository {
    override fun getUserId() = sharedPreferencesManager.get(PREFS_USER_ID, "")
    override fun saveUserId(userId: String) {
        sharedPreferencesManager.save(PREFS_USER_ID, userId)
    }

    override fun getIpAddress() = sharedPreferencesManager.get(PREFS_IP_INPUT, "")
    override fun saveIpAddress(ipAddress: String) {
        sharedPreferencesManager.save(PREFS_IP_INPUT, ipAddress)
    }

    override fun getPortNumber() = sharedPreferencesManager.get(PREFS_PORT_INPUT, "")
    override fun savePortNumber(portNumber: String) {
        sharedPreferencesManager.save(PREFS_PORT_INPUT, portNumber)
    }
}