package com.dtakac.aux_remote.app_songs_pager.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.app_songs_pager.pager.wrapper.UserQueuedSongUi
import com.dtakac.aux_remote.base.resource_repo.ResourceRepository
import com.dtakac.aux_remote.base.prefs.SharedPrefsRepository
import com.dtakac.aux_remote.common.network.ClientSocket
import com.dtakac.aux_remote.common.database_repository.DatabaseRepository
import com.dtakac.aux_remote.app_songs_pager.all_songs.wrapper.SongWrapper
import com.dtakac.aux_remote.app_songs_pager.pager.wrapper.provideUserQueuedSongUi
import com.dtakac.aux_remote.app_songs_pager.queue.wrapper.QueueUi
import com.dtakac.aux_remote.app_songs_pager.queue.wrapper.QueuedSongWrapper
import com.dtakac.aux_remote.app_songs_pager.queue.wrapper.provideQueueUi
import com.dtakac.aux_remote.common.constants.*
import com.dtakac.aux_remote.common.extensions.forceRefresh
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class SongsPagerViewModel(
    private val repo: DatabaseRepository,
    private val prefsRepo: SharedPrefsRepository,
    private val clientSocket: ClientSocket,
    private val resourceRepo: ResourceRepository
) : ViewModel(){

    //region songs view
    val songsLiveData = MutableLiveData<List<SongWrapper>>()
    val filteredSongsLiveData = MutableLiveData<List<SongWrapper>>()
    private var userSentSong = false

    fun getAllSongs() = repo.getSongs().doOnNext { songsLiveData.value = it }

    fun onSongClicked(songName: String) {
        userSentSong = true
        CoroutineScope(IO).launch{
            writeSongToServer(songName)
        }
    }

    fun onQueryTextChanged(query: String){
        // filter song names which contain query string
        CoroutineScope(Default).launch {
            val filtered = songsLiveData.value
                ?.filter { it.name.contains(query, ignoreCase = true) }
                ?.map {
                    SongWrapper(
                        it.id,
                        it.name,
                        query,
                        resourceRepo.getColor(R.color.green400_analogous)
                    )
                }
                ?.toList()

            withContext(Main){
                filteredSongsLiveData.value = filtered
            }
        }
    }

    fun onSearchCollapsed() = songsLiveData.forceRefresh()

    private fun writeSongToServer(songName: String){
        val writer = BufferedWriter(OutputStreamWriter(clientSocket.outputStream ?: return, StandardCharsets.UTF_8))
        writer.apply {
            write(CLIENT_QUEUE); newLine()
            write(prefsRepo.get(PREFS_USER_ID, "")); newLine()
            write(songName); newLine()
            flush()
        }
    }
    //endregion

    //region queue view
    val queueLiveData = MutableLiveData<QueueUi>()
    val userQueuedSongLiveData = MutableLiveData<UserQueuedSongUi>()

    fun getQueuedSongs() = repo.getQueuedSongs()
        .doOnNext {
            queueLiveData.value = provideQueueUi(it, queueLiveData.value?.nowPlayingSong)
            updateUserQueuedSongLiveData(it)
        }

    private fun updateUserQueuedSongLiveData(queuedSongs: List<QueuedSongWrapper>){
        val userSong = queuedSongs.firstOrNull { song ->
            song.ownerId == prefsRepo.get(PREFS_USER_ID, "")
        }
        val previousUserSong = userQueuedSongLiveData.value?.queuedSong

        if(userSong != null && userSentSong){
            // if the previous user song in queue isnt null and user sent a song,
            // that means he swapped his previous song for a new one.
            val userSwappedSong = previousUserSong != null
            userQueuedSongLiveData.value =
                provideUserQueuedSongUi(
                    userSwappedSong,
                    userSong
                )
            userSentSong = false
        }
    }

    fun getNowPlayingSong() = repo.getNowPlayingSong()
        .doOnNext {
            queueLiveData.value = provideQueueUi(queueLiveData.value?.queuedSongs, it)
        }
    //endregion

    //region pull from server
    fun pullFromServer() = CoroutineScope(IO).launch {
            writeRequests(listOf(
                CLIENT_REQUEST_SONGS,
                CLIENT_REQUEST_QUEUE,
                CLIENT_REQUEST_PLAYING
            )
            )
        }

    private fun writeRequests(requests: List<String>){
        val writer = BufferedWriter(OutputStreamWriter(clientSocket.outputStream ?: return, StandardCharsets.UTF_8))
        writer.apply{
            requests.forEach { write(it); newLine(); flush() }
        }
    }
    //endregion
}