package com.dtakac.aux_remote.server

import com.dtakac.aux_remote.common.constants.*
import com.dtakac.aux_remote.common.repository.DatabaseRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.lang.IllegalStateException
import java.nio.charset.StandardCharsets

class AuxServerInteractor(
    private val serverSocket: ServerSocket,
    private val repository: DatabaseRepository
): ServerInteractor {
    private var reader: BufferedReader? = null
    private var writer: BufferedWriter? = null

    override fun initializeReaderAndWriter(): Boolean {
        reader = BufferedReader(InputStreamReader(
            serverSocket.inputStream ?: return false,
            StandardCharsets.UTF_8)
        )
        writer = BufferedWriter(OutputStreamWriter(
            serverSocket.outputStream ?: return false,
            StandardCharsets.UTF_8)
        )
        return true
    }

    private suspend fun writeLineToServer(line: String) {
        withContext(IO){
            writer?.apply { write(line); newLine(); flush() }
        }
    }

    override suspend fun requestPlayerState() {
        writeLineToServer(CLIENT_REQUEST_SONGS)
        writeLineToServer(CLIENT_REQUEST_QUEUE)
        writeLineToServer(CLIENT_REQUEST_PLAYING)
    }

    override suspend fun writeSongToServer(userId: String, songName: String) {
        withContext(IO) {
            writer?.apply {
                write(CLIENT_QUEUE); newLine()
                write(userId); newLine()
                write(songName); newLine()
                flush()
            }
        }
    }

    override suspend fun processNextServerResponse() {
        val response = mutableListOf<String>()
        withContext(IO) {
            //reads server response
            while(true){
                val line = reader?.readLine() ?: throw IllegalStateException("Line is null.")
                if(line == SERVER_BROADCAST_END) break else response.add(line)
            }
            //puts it into repository
            if(response.isNotEmpty()) {
                val body = response.subList(1, response.size)
                when (response[0]) {
                    SERVER_SONG_LIST -> repository.insertSongs(body)
                    SERVER_QUEUE_LIST -> repository.insertQueuedSongs(body)
                    SERVER_ENQUEUED -> repository.insertQueuedSong(body)
                    SERVER_MOVE_UP -> repository.moveUp()
                    SERVER_NOW_PLAYING -> repository.updateNowPlayingSong(body)
                }
            }
        }
    }

    override suspend fun initializeSocket(ipAddress: String, port: String): Boolean {
        val portNum = try { Integer.parseInt(port) } catch (e: Exception) { return false }
        var success = false
        withContext(IO){ success = serverSocket.initialize(ipAddress, portNum) }
        return success
    }

    override suspend fun connectToServer(userId: String) {
        withContext(IO){
            writer?.apply{
                write(CLIENT_MAC); newLine()
                write(userId); newLine()
                flush()
            }
        }
    }

    override suspend fun closeSocket() {
        var success = false
        withContext(IO){
            success = serverSocket.close()
        }
        if(success){
            writer = null
            reader = null
        }
    }
}