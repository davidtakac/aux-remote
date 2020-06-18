package com.dtakac.aux_remote.server

import com.dtakac.aux_remote.common.constants.CLIENT_MAC
import com.dtakac.aux_remote.common.constants.CLIENT_QUEUE
import com.dtakac.aux_remote.common.constants.SERVER_BROADCAST_END
import com.dtakac.aux_remote.common.network.ServerSocket
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.lang.IllegalStateException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class AuxServerInteractor(
    private val serverSocket: ServerSocket
): ServerInteractor {
    private var reader: BufferedReader? = null
    private var writer: BufferedWriter? = null

    override fun initializeReaderAndWriter(): Boolean {
        reader = BufferedReader(InputStreamReader(
            serverSocket.inputStream ?: return false,
            Charset.forName("UTF-8"))
        )
        writer = BufferedWriter(BufferedWriter(OutputStreamWriter(
            serverSocket.outputStream ?: return false,
            StandardCharsets.UTF_8))
        )
        return true
    }

    override suspend fun writeLineToServer(line: String) {
        withContext(IO){
            writer?.apply { write(line); newLine(); flush() }
        }
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

    override suspend fun getNextData(): List<String> {
        val result = mutableListOf<String>()
        withContext(IO) {
            while(true){
                val line = reader?.readLine() ?: throw IllegalStateException("Line is null.")
                if(line == SERVER_BROADCAST_END) break else result.add(line)
            }
        }
        return result
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