package com.dtakac.aux_remote.songs_pager.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.ResourceRepo
import com.dtakac.aux_remote.base.SharedPrefsRepo
import com.dtakac.aux_remote.common.*
import com.dtakac.aux_remote.data.now_playing_song.NowPlayingSongDao
import com.dtakac.aux_remote.data.queued_song.QueuedSongDao
import com.dtakac.aux_remote.network.ClientSocket
import com.dtakac.aux_remote.repository.Repository
import com.dtakac.aux_remote.songs_pager.all_songs.wrapper.SongWrapper
import com.dtakac.aux_remote.songs_pager.queue.wrapper.NowPlayingSongWrapper
import com.dtakac.aux_remote.songs_pager.queue.wrapper.QueueUi
import com.dtakac.aux_remote.songs_pager.queue.wrapper.QueuedSongWrapper
import com.dtakac.aux_remote.songs_pager.queue.wrapper.provideQueueUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class SongsPagerViewModel(
    private val repo: Repository,
    private val prefsRepo: SharedPrefsRepo,
    private val client: ClientSocket,
    private val resourceRepo: ResourceRepo
) : ViewModel(){

    val songsLiveData = MutableLiveData<List<SongWrapper>>()
    val filteredSongsLiveData = MutableLiveData<List<SongWrapper>>()

    val queueLiveData = MutableLiveData<QueueUi>()
    val userQueuedSongLiveData = MutableLiveData<QueuedSongWrapper>()

    //region songs fragment
    fun getAllSongs() = repo.getSongs().doOnNext { songsLiveData.value = it }

    fun onSongClicked(songName: String){
        CoroutineScope(IO).launch{
            val outputStream = client.outputStream!!
            writeSongToServer(songName, outputStream)
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

    private fun writeSongToServer(songName: String, outputStream: OutputStream){
        val writer = BufferedWriter(OutputStreamWriter(outputStream, StandardCharsets.UTF_8))
        writer.write(CLIENT_QUEUE)
        writer.newLine()
        writer.write(prefsRepo.get(PREFS_USER_ID, ""))
        writer.newLine()
        writer.write(songName)
        writer.newLine()
        writer.flush()
    }
    //endregion

    //region queue fragment
    fun getQueuedSongs() = repo.getQueuedSongs()
        .doOnNext {
            queueLiveData.value = provideQueueUi(it, queueLiveData.value?.nowPlayingSong)

            val userSong = it.firstOrNull { song -> song.ownerId == prefsRepo.get(PREFS_USER_ID, "") }
            if(userSong != null && userSong.name != userQueuedSongLiveData.value?.name){
                userQueuedSongLiveData.value = userSong
            }
        }

    fun getNowPlayingSong() = repo.getNowPlayingSong()
        .doOnNext {
            queueLiveData.value = provideQueueUi(queueLiveData.value?.queuedSongs, it)
        }
    //endregion
}