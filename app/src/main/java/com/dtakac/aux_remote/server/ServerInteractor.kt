package com.dtakac.aux_remote.server

interface ServerInteractor {
    fun initializeReaderAndWriter(): Boolean
    suspend fun requestPlayerState()
    suspend fun sendSong(userId: String, songName: String)
    suspend fun processNextResponse()
    suspend fun initializeConnection(ipAddress: String, port: String): Boolean
    suspend fun connect(userId: String)
    suspend fun closeConnection()
    fun addServerEventListener(listener: ServerEventListener)
    fun removeServerEventListener(listener: ServerEventListener)
}