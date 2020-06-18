package com.dtakac.aux_remote.server

interface ServerInteractor {
    fun initReaderAndWriter(): Boolean
    suspend fun writeLineToServer(line: String)
    suspend fun writeSongToServer(userId: String, songName: String)
    suspend fun getNextData(): List<String>
}