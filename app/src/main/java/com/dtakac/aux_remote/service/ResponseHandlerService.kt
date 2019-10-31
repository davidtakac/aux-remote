package com.dtakac.aux_remote.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.dtakac.aux_remote.common.SERVER_BROADCAST_END
import com.dtakac.aux_remote.common.SERVER_SONG_LIST
import com.dtakac.aux_remote.data.AppDatabase
import com.dtakac.aux_remote.data.song.Song
import com.dtakac.aux_remote.data.song.SongDao
import com.dtakac.aux_remote.network.ClientSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.SocketException
import java.nio.charset.Charset

private const val TAG = "service_tag"
private const val JOB_ID = 71169
private const val SERVICE_ACTION = "RESPONSE_HANDLER"
class ResponseHandlerService: JobIntentService(){
    private val socket by inject<ClientSocket>()
    private val songDao by inject<SongDao>()

    companion object{
        fun start(context: Context){
            JobIntentService.enqueueWork(
                context,
                ResponseHandlerService::class.java,
                JOB_ID, Intent(SERVICE_ACTION)
            )
        }
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
                readServerResponse(reader)
            } catch (se: SocketException){
                Log.e(TAG, "Socket threw exception, stopping service..")
                se.printStackTrace()
                break
            }
        }
    }

    private fun readServerResponse(reader: BufferedReader){
        val lines = mutableListOf<String>()
        while(true){
            val line = reader.readLine()
            if(line != SERVER_BROADCAST_END) lines.add(line) else break
        }
        handleServerResponse(lines)
    }

    private fun handleServerResponse(lines: List<String>){
        when(lines[0]){
            SERVER_SONG_LIST -> onSongList(lines.subList(1, lines.size))
        }
    }

    private fun onSongList(songNames: List<String>){
        songDao.insertAll(songNames.map { Song(name = it) }.toList())
    }
}