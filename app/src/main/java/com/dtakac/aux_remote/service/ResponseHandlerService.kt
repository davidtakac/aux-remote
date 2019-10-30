package com.dtakac.aux_remote.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.dtakac.aux_remote.data.AppDatabase
import com.dtakac.aux_remote.data.Song
import com.dtakac.aux_remote.network.ClientSocket
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
    private val db by inject<AppDatabase>()

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
        val stream = socket.inputStream ?: throw IllegalStateException("Service started, but socket input stream is null.")
        val reader = BufferedReader(InputStreamReader(stream, Charset.forName("UTF-8")))

        try {
            while (true) {
                val line = reader.readLine()
                Log.d(TAG, line)
                db.songDao().insert(Song(name=line))
            }
        } catch (s: SocketException){
            Log.e(TAG, "Socket exception occurred, stopping service.")
            s.printStackTrace()
        }

        db.songDao().deleteAll()
    }
}