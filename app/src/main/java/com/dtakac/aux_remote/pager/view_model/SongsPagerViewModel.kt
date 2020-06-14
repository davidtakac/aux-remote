package com.dtakac.aux_remote.pager.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.resource_repo.ResourceRepository
import com.dtakac.aux_remote.common.base.prefs.SharedPrefsRepository
import com.dtakac.aux_remote.common.network.ClientSocket
import com.dtakac.aux_remote.common.database_repository.DatabaseRepository
import com.dtakac.aux_remote.pager.songs.wrapper.SongWrapper
import com.dtakac.aux_remote.common.constants.*
import com.dtakac.aux_remote.pager.songs.mode.SongsMode
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
    val songs = repo.getSongs()
    val nowPlayingSong = repo.getNowPlayingSong()
    val queue = repo.getQueuedSongs()

    private val _filteredSongs = MutableLiveData<List<SongWrapper>>()
    private val _songsMode = MutableLiveData<SongsMode>()
    val filteredSongs: LiveData<List<SongWrapper>> = _filteredSongs
    val songsMode: LiveData<SongsMode> = _songsMode

    fun onSongClicked(songName: String){
        CoroutineScope(IO).launch { writeSongToServer(songName) }
    }

    fun onQueryTextChanged(query: String) {
        CoroutineScope(Default).launch {
            val filtered = filterSongs(query)
            withContext(Main){
                _filteredSongs.value = filtered
                _songsMode.value = SongsMode.FILTERED_SONGS
            }
        }
    }

    fun onSearchCollapsed(){
        _songsMode.value = SongsMode.SONGS
    }

    fun pullFromServer() {
        CoroutineScope(IO).launch {
            writeLineToServer(CLIENT_REQUEST_SONGS)
            writeLineToServer(CLIENT_REQUEST_QUEUE)
            writeLineToServer(CLIENT_REQUEST_PLAYING)
        }
    }

    private fun writeLineToServer(line: String){
        val writer = BufferedWriter(OutputStreamWriter(clientSocket.outputStream ?: return, StandardCharsets.UTF_8))
        writer.apply {
            write(line); newLine();
            flush()
        }
    }

    private fun writeSongToServer(songName: String){
        val writer = BufferedWriter(OutputStreamWriter(clientSocket.outputStream ?: return, StandardCharsets.UTF_8))
        writer.apply {
            write(CLIENT_QUEUE); newLine()
            write(prefsRepo.get(PREFS_USER_ID, "")); newLine()
            write(songName); newLine()
            flush()
        }
    }

    private fun filterSongs(query: String): List<SongWrapper>?{
        return songs.value
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
    }
}