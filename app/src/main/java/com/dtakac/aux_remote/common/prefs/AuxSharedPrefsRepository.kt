package com.dtakac.aux_remote.common.prefs

interface AuxSharedPrefsRepository {
    fun getUserId(): String
    fun saveUserId(userId: String)
    fun getIpAddress(): String
    fun saveIpAddress(ipAddress: String)
    fun getPortNumber(): String
    fun savePortNumber(portNumber: String)
}