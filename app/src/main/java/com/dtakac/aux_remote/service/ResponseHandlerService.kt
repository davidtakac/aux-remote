package com.dtakac.aux_remote.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.dtakac.aux_remote.network.ServerSocket
import org.koin.android.ext.android.inject

private const val TAG = "servicetag"
private const val JOB_ID = 71169
private const val SERVICE_ACTION = "RESPONSE_HANDLER"
class ResponseHandlerService: JobIntentService(){
    private val socket by inject<ServerSocket>()

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
            Thread.sleep(1000)
            Log.d(TAG, "job intent service running")
        }
    }
}