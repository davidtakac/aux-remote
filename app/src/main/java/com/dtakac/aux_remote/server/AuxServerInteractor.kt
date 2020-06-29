package com.dtakac.aux_remote.server

import com.dtakac.aux_remote.common.repository.Repository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.nio.charset.StandardCharsets
import android.util.Log
import kotlin.IllegalStateException

// CLIENT KEYS
private const val CLIENT_MAC = "CLIENT_MAC_ADDRESS"
private const val CLIENT_QUEUE = "CLIENT_QUEUE"
private const val CLIENT_REQUEST_SONGS = "CLIENT_SONGS_REQUEST"
private const val CLIENT_REQUEST_QUEUE = "CLIENT_QUEUE_REQUEST"
private const val CLIENT_REQUEST_PLAYING = "CLIENT_NOW_PLAYING_REQUEST"

// SERVER KEYS
private const val SERVER_BROADCAST_END = "SERVER_BROADCAST_ENDED"
private const val SERVER_SONG_LIST = "SERVER_SONG_LIST"
private const val SERVER_QUEUE_LIST = "SERVER_QUEUE_LIST"
private const val SERVER_ENQUEUED = "SERVER_ENQUEUED"
private const val SERVER_MOVE_UP = "SERVER_MOVE_UP"
private const val SERVER_NOW_PLAYING = "SERVER_NOW_PLAYING"

class AuxServerInteractor(
    private val serverSocket: ServerSocket,
    private val repository: Repository
): ServerInteractor {
    private var reader: BufferedReader? = null
    private var writer: BufferedWriter? = null
    private var listeners = mutableListOf<ServerEventListener>()

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

    override suspend fun sendSong(userId: String, songName: String) {
        withContext(IO) {
            writer?.apply {
                write(CLIENT_QUEUE); newLine()
                write(userId); newLine()
                write(songName); newLine()
                flush()
            }
        }
    }

    override suspend fun processNextResponse() {
        val response = mutableListOf<String>()
        withContext(IO) {
            //reads server response
            while(true){
                val line = reader?.readLine() ?: throw IllegalStateException("Line is null.")
                if(line == SERVER_BROADCAST_END) break else response.add(line)
            }
            Log.d("server_response", response.toString())
            //puts it into repository
            if(response.isNotEmpty()) {
                val body = response.subList(1, response.size)
                when (response[0]) {
                    SERVER_SONG_LIST -> repository.insertSongs(body)
                    SERVER_QUEUE_LIST -> repository.insertQueuedSongs(body)
                    SERVER_ENQUEUED -> repository.insertQueuedSong(body)
                    SERVER_MOVE_UP -> repository.moveUp()
                    SERVER_NOW_PLAYING -> handleNowPlayingSong(body)
                    else -> throw IllegalStateException("${response[0]} is unknown server code.")
                }
            }
        }
    }

    override suspend fun initializeConnection(ipAddress: String, port: String): Boolean {
        val portNum = try { Integer.parseInt(port) } catch (e: Exception) { return false }
        var success = false
        withContext(IO){ success = serverSocket.initialize(ipAddress, portNum) }
        return success
    }

    override suspend fun connect(userId: String) {
        withContext(IO){
            writer?.apply{
                write(CLIENT_MAC); newLine()
                write(userId); newLine()
                flush()
            }
        }
    }

    override suspend fun closeConnection() {
        var success = false
        withContext(IO){
            success = serverSocket.close()
        }
        if(success){
            writer = null
            reader = null
        }
    }

    override fun addServerEventListener(listener: ServerEventListener) {
        listeners.add(listener)
    }

    override fun removeServerEventListener(listener: ServerEventListener) {
        listeners.remove(listener)
    }

    private suspend fun handleNowPlayingSong(body: List<String>){
        val newNowPlayingSong = withContext(IO) { repository.updateNowPlayingSong(body) }
        listeners.forEach { it.onNowPlayingSongChanged(newNowPlayingSong) }
    }
}