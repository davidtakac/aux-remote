package com.dtakac.aux_remote.service

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.app.JobIntentService
import com.dtakac.aux_remote.base.SharedPrefsRepo
import com.dtakac.aux_remote.common.*
import com.dtakac.aux_remote.data.now_playing_song.NowPlayingSong
import com.dtakac.aux_remote.data.now_playing_song.NowPlayingSongDao
import com.dtakac.aux_remote.data.queued_song.QueuedSong
import com.dtakac.aux_remote.data.queued_song.QueuedSongDao
import com.dtakac.aux_remote.data.song.Song
import com.dtakac.aux_remote.data.song.SongDao
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
    private val songDao by inject<SongDao>()
    private val queuedSongDao by inject<QueuedSongDao>()
    private val nowPlayingDao by inject<NowPlayingSongDao>()
    private val prefsRepo by inject<SharedPrefsRepo>()

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
            } catch (se: SocketException){
                Log.e(TAG, "Socket threw exception, stopping service..")
                se.printStackTrace()
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

    // each line is server song
    private fun onSongList(response: List<String>){
        songDao.insertAll(response.map { Song(name = it) }.toList())
    }

    // queued song first, then owner id
    private fun onQueueList(response: List<String>){
        val result = mutableListOf<QueuedSong>()
        for(i in response.indices step 2){
            val name = response[i]
            val ownerId = response[i+1]
            val userIconVisibility = if(ownerId == prefsRepo.get(PREFS_USER_ID, "")) View.VISIBLE else View.GONE
            result.add(QueuedSong(ownerId, name, userIconVisibility, i/2))
        }

        queuedSongDao.insertAllOrUpdate(result)
    }

    // name of song first, then owner id, then position in queue
    private fun onEnqueued(response: List<String>){
        val songName = response[0]
        val ownerId = response[1]
        val position = response[2].toInt()
        val userIconVisibility = if(ownerId == prefsRepo.get(PREFS_USER_ID, "")) View.VISIBLE else View.GONE

        val queuedSong = QueuedSong(ownerId, songName, userIconVisibility, position)
        queuedSongDao.insertOrUpdate(queuedSong)
    }

    private fun onMoveUp(){
        queuedSongDao.moveUp()
    }

    // song name first, then owner id
    private fun onNowPlaying(response: List<String>){
        val songName = response[0]
        val ownerId = response[1]

        val nowPlayingSong = NowPlayingSong(
            name = songName,
            isUserSong = ownerId == prefsRepo.get(PREFS_USER_ID, "")
        )
        nowPlayingDao.setNowPlayingSong(nowPlayingSong)
    }
}