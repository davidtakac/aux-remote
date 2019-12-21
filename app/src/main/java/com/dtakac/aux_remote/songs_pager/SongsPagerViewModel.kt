package com.dtakac.aux_remote.songs_pager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.ResourceRepo
import com.dtakac.aux_remote.base.SharedPrefsRepo
import com.dtakac.aux_remote.common.*
import com.dtakac.aux_remote.data.now_playing_song.NowPlayingSong
import com.dtakac.aux_remote.data.now_playing_song.NowPlayingSongDao
import com.dtakac.aux_remote.data.queued_song.QueuedSong
import com.dtakac.aux_remote.data.queued_song.QueuedSongDao
import com.dtakac.aux_remote.network.ClientSocket
import com.dtakac.aux_remote.repository.Repository
import com.dtakac.aux_remote.songs_pager.all_songs.SongWrapper
import com.dtakac.aux_remote.songs_pager.queue.QueueUi
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
    private val queuedSongDao: QueuedSongDao,
    private val nowPlayingSongDao: NowPlayingSongDao,
    private val prefsRepo: SharedPrefsRepo,
    private val client: ClientSocket,
    private val resourceRepo: ResourceRepo
) : ViewModel(){

    val songsLiveData = MutableLiveData<List<SongWrapper>>()
    val filteredSongsLiveData = MutableLiveData<List<SongWrapper>>()

    private val _queueLiveData = MutableLiveData<QueueUi>().apply {
        value = QueueUi(listOf(), NowPlayingSong())
    }
    val queueLiveData: LiveData<QueueUi> = _queueLiveData

    private val _userQueuedSongLiveData = MutableLiveData<QueuedSong>()
    val userQueuedSongLiveData: LiveData<QueuedSong> = _userQueuedSongLiveData

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
                ?.map { it.song }
                ?.filter { it.name.contains(query, ignoreCase = true) }
                ?.map { SongWrapper(it, query, resourceRepo.getColor(R.color.green400_analogous)) }
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
    fun getQueuedSongs() = queuedSongDao.getQueuedSongs().defaultSchedulers()
        .doOnNext{
            _queueLiveData.value?.queuedSongs = it
            _queueLiveData.forceRefresh()

            val userSong = it.firstOrNull { song -> song.ownerId == prefsRepo.get(PREFS_USER_ID, "") }
            if(userSong != null && userSong.name != _userQueuedSongLiveData.value?.name){
                _userQueuedSongLiveData.value = userSong
            }
        }

    fun getNowPlayingSong() = nowPlayingSongDao.getNowPlayingSong().defaultSchedulers()
        .doOnNext {
            _queueLiveData.value?.nowPlayingSong = it
            _queueLiveData.forceRefresh()
        }
    //endregion
}