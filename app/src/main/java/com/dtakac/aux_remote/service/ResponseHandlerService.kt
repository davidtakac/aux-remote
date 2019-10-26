package com.dtakac.aux_remote.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.dtakac.aux_remote.network.ClientSocket
import org.koin.android.ext.android.inject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

private const val TAG = "servicetag"
private const val JOB_ID = 71169
private const val SERVICE_ACTION = "RESPONSE_HANDLER"
class ResponseHandlerService: JobIntentService(){
    private val socket by inject<ClientSocket>()

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
        while(true){
            val stream = socket.inputStream ?: continue

            val reader = BufferedReader(InputStreamReader(stream, Charset.forName("UTF-8")))
            Log.d(TAG, reader.readLine())
        }
    }
}