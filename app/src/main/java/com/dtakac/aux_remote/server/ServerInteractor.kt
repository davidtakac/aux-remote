package com.dtakac.aux_remote.server

interface ServerInteractor {
    fun initializeReaderAndWriter(): Boolean
    suspend fun requestPlayerState()
    suspend fun writeSongToServer(userId: String, songName: String)
    suspend fun processNextServerResponse()
    suspend fun initializeSocket(ipAddress: String, port: String): Boolean
    suspend fun connectToServer(userId: String)
    suspend fun closeSocket()
}