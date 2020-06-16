package com.dtakac.aux_remote.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.dtakac.aux_remote.common.constants.*
import com.dtakac.aux_remote.common.network.ClientSocket
import com.dtakac.aux_remote.common.repository.Repository
import org.koin.android.ext.android.inject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.IllegalStateException
import java.nio.charset.Charset
import kotlin.Exception

private const val TAG = "service_tag"
private const val JOB_ID = 71169
private const val SERVICE_ACTION = "RESPONSE_HANDLER"
class ResponseHandlerService: JobIntentService(){
    private val socket by inject<ClientSocket>()
    private val repo by inject<Repository>()

    companion object{
        fun start(context: Context) {
            enqueueWork(context, ResponseHandlerService::class.java, JOB_ID, Intent(SERVICE_ACTION))
        }
    }

    override fun onHandleWork(intent: Intent) {
        val stream = socket.inputStream
        var messageText: String? = null
        if(stream != null) {
            val reader = BufferedReader(InputStreamReader(stream, Charset.forName("UTF-8")))
            while (true) {
                try {
                    handleServerResponse(readServerResponse(reader))
                } catch (e: Exception) {
                    messageText = e.message
                    Log.e(TAG, "Exception in service loop, stopping service. Message: ${e.message}")
                    e.printStackTrace()
                    break
                }
            }
        } else {
            Log.e(TAG, "Socket input stream is null, stopped service.")
        }
        onServiceStopped(messageText)
    }

    private fun readServerResponse(reader: BufferedReader): List<String>{
        val lines = mutableListOf<String>()
        while(true){
            val line = reader.readLine() ?: throw IllegalStateException("Line is null.")
            if(line == SERVER_BROADCAST_END) break else lines.add(line)
        }
        Log.d(TAG, lines.toString())
        return lines
    }

    //todo: bug when player doesnt have loaded songs
    private fun handleServerResponse(lines: List<String>){
        if(lines.isNotEmpty()) {
            val body = lines.subList(1, lines.size)
            when (lines[0]) {
                SERVER_SONG_LIST -> onSongList(body)
                SERVER_QUEUE_LIST -> onQueueList(body)
                SERVER_ENQUEUED -> onEnqueued(body)
                SERVER_MOVE_UP -> onMoveUp()
                SERVER_NOW_PLAYING -> onNowPlaying(body)
            }
        }
    }

    private fun onSongList(body: List<String>){
        repo.insertSongs(body)
    }

    private fun onQueueList(body: List<String>){
        repo.insertQueuedSongs(body)
    }

    private fun onEnqueued(body: List<String>){
        repo.insertQueuedSong(body)
    }

    private fun onMoveUp(){
        repo.moveUp()
    }

    private fun onNowPlaying(response: List<String>){
        repo.updateNowPlayingSong(response)
    }

    private fun onServiceStopped(messageText: String?){
        repo.updateMessage(messageText ?: EMPTY_STRING)
    }
}