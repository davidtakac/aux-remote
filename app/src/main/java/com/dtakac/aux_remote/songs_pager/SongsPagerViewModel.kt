package com.dtakac.aux_remote.songs_pager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtakac.aux_remote.base.SharedPrefsRepo
import com.dtakac.aux_remote.common.CLIENT_MAC
import com.dtakac.aux_remote.common.CLIENT_QUEUE
import com.dtakac.aux_remote.common.update
import com.dtakac.aux_remote.data.Song
import com.dtakac.aux_remote.data.SongDao
import com.dtakac.aux_remote.network.ClientSocket
import com.dtakac.aux_remote.songs_pager.all_songs.AllSongsUi
import com.dtakac.aux_remote.songs_pager.all_songs.provideAllSongsUi
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class SongsPagerViewModel(
    private val songDao: SongDao,
    private val prefsRepo: SharedPrefsRepo,
    private val client: ClientSocket
) : ViewModel(){

    private val _songsLiveData = MutableLiveData<AllSongsUi>()
    val songsLiveData: LiveData<AllSongsUi> = _songsLiveData

    fun getAllSongs(): Observable<List<Song>> =
        songDao.getAll()
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
        writer.write(prefsRepo.get(CLIENT_MAC, ""))
        writer.newLine()
        writer.write(song.name)
        writer.newLine()
        writer.flush()
    }
}