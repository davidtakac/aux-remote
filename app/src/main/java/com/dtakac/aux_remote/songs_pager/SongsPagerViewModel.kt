package com.dtakac.aux_remote.songs_pager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtakac.aux_remote.base.SharedPrefsRepo
import com.dtakac.aux_remote.common.*
import com.dtakac.aux_remote.data.now_playing_song.NowPlayingSong
import com.dtakac.aux_remote.data.now_playing_song.NowPlayingSongDao
import com.dtakac.aux_remote.data.queued_song.QueuedSongDao
import com.dtakac.aux_remote.data.song.Song
import com.dtakac.aux_remote.data.song.SongDao
import com.dtakac.aux_remote.network.ClientSocket
import com.dtakac.aux_remote.songs_pager.all_songs.AllSongsUi
import com.dtakac.aux_remote.songs_pager.all_songs.provideAllSongsUi
import com.dtakac.aux_remote.songs_pager.queue.QueueUi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class SongsPagerViewModel(
    private val songDao: SongDao,
    private val queuedSongDao: QueuedSongDao,
    private val nowPlayingSongDao: NowPlayingSongDao,
    private val prefsRepo: SharedPrefsRepo,
    private val client: ClientSocket
) : ViewModel(){

    private val _songsLiveData = MutableLiveData<AllSongsUi>()
    val songsLiveData: LiveData<AllSongsUi> = _songsLiveData

    private val _queueLiveData = MutableLiveData<QueueUi>().apply { value = QueueUi(listOf(), NowPlayingSong()) }
    val queueLiveData: LiveData<QueueUi> = _queueLiveData

    //region songs fragment
    fun getAllSongs(): Observable<List<Song>> =
        songDao.getAll().defaultSchedulers()
            .doOnNext {
                _songsLiveData.value = provideAllSongsUi(
                    it,
                    _songsLiveData.value?.filteredSongs ?: listOf(),
                    _songsLiveData.value?.isSearching ?: false
                )
            }

    fun onSongClicked(songId: Int){
        CoroutineScope(IO).launch{
            val song = songDao.get(songId) ?: return@launch
            val outputStream = client.outputStream ?: return@launch
            writeSongToServer(song, outputStream)
        }
    }

    fun onSearchViewExpanded(){
        _songsLiveData.value?.isSearching = true
    }

    fun onSearchViewCollapsed(){
        _songsLiveData.value?.isSearching = false
        _songsLiveData.update()
    }

    fun onQueryTextChanged(query: String){
        // filter song names which contain query string
        _songsLiveData.value?.let {
            it.filteredSongs = it.songs.filter { song -> song.name.contains(query, ignoreCase = true) }.toList()
        }
        _songsLiveData.update()
    }

    private fun writeSongToServer(song: Song, outputStream: OutputStream){
        val writer = BufferedWriter(OutputStreamWriter(outputStream, StandardCharsets.UTF_8))
        writer.write(CLIENT_QUEUE)
        writer.newLine()
        writer.write(prefsRepo.get(PREFS_USER_ID, ""))
        writer.newLine()
        writer.write(song.name)
        writer.newLine()
        writer.flush()
    }
    //endregion

    //region queue fragment
    fun getQueuedSongs() = queuedSongDao.getQueuedSongsOldestFirst().defaultSchedulers()
        .doOnNext{
            _queueLiveData.value?.queuedSongs = it
            _queueLiveData.update()
        }

    fun getNowPlayingSong() = nowPlayingSongDao.getNowPlayingSong().defaultSchedulers()
        .doOnNext {
            _queueLiveData.value?.nowPlayingSong = it
            _queueLiveData.update()
        }
    //endregion
}