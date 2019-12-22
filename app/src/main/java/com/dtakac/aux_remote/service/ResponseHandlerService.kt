package com.dtakac.aux_remote.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.dtakac.aux_remote.common.*
import com.dtakac.aux_remote.network.ClientSocket
import com.dtakac.aux_remote.common.database_repository.DatabaseRepository
import org.koin.android.ext.android.inject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.nio.charset.Charset

private const val TAG = "service_tag"
private const val JOB_ID = 71169
private const val SERVICE_ACTION = "RESPONSE_HANDLER"
class ResponseHandlerService: JobIntentService(){
    private val socket by inject<ClientSocket>()
    private val repo by inject<DatabaseRepository>()

    companion object{
        fun start(context: Context) =
            enqueueWork(context, ResponseHandlerService::class.java, JOB_ID, Intent(SERVICE_ACTION))
    }

    override fun onHandleWork(intent: Intent) {
        val stream = socket.inputStream
        if(stream == null){
            Log.e(TAG, "Service started, but socket input stream is null. Stopping service.")
            return
        }

        val reader = BufferedReader(InputStreamReader(stream, Charset.forName("UTF-8")))
        while (true) {
            try {
                handleServerResponse(readServerResponse(reader))
            } catch (e: Exception){
                Log.e(TAG, "Socket exception in service loop, stopping service..")
                e.printStackTrace()
                break
            }
        }
    }

    private fun readServerResponse(reader: BufferedReader): List<String>{
        val lines = mutableListOf<String>()
        while(true){
            val line = reader.readLine()
            if(line == null || line == SERVER_BROADCAST_END) break else lines.add(line)
        }
        Log.d(TAG, lines.toString())
        return lines
    }

    private fun handleServerResponse(lines: List<String>){
        if(lines.isEmpty()) return

        val body = lines.subList(1, lines.size)
        when(lines[0]){
            SERVER_SONG_LIST -> onSongList(body)
            SERVER_QUEUE_LIST -> onQueueList(body)
            SERVER_ENQUEUED -> onEnqueued(body)
            SERVER_MOVE_UP -> onMoveUp()
            SERVER_NOW_PLAYING -> onNowPlaying(body)
        }
    }

    private fun onSongList(response: List<String>){
        repo.persistSongs(response)
    }

    private fun onQueueList(response: List<String>){
        repo.persistQueuedSongs(response)
    }

    private fun onEnqueued(response: List<String>){
        repo.persistQueuedSong(response)
    }

    private fun onMoveUp(){
        repo.moveUp()
    }

    private fun onNowPlaying(response: List<String>){
        repo.persistNowPlayingSong(response)
    }
}