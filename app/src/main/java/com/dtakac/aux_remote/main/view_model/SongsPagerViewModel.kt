package com.dtakac.aux_remote.main.view_model

import android.view.View
import androidx.lifecycle.*
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.resource.ResourceRepository
import com.dtakac.aux_remote.common.base.prefs.SharedPrefsRepository
import com.dtakac.aux_remote.common.network.ClientSocket
import com.dtakac.aux_remote.common.repository.Repository
import com.dtakac.aux_remote.main.songs.wrapper.SongWrapper
import com.dtakac.aux_remote.common.constants.*
import com.dtakac.aux_remote.main.common.FeedbackMessage
import com.dtakac.aux_remote.main.common.SongsMode
import com.dtakac.aux_remote.main.queue.wrapper.NowPlayingSongWrapper
import com.dtakac.aux_remote.main.queue.wrapper.QueuedSongWrapper
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
    private val repo: Repository,
    private val prefsRepo: SharedPrefsRepository,
    private val clientSocket: ClientSocket,
    private val resourceRepo: ResourceRepository
) : ViewModel(){
    //privately mutable
    private val _filteredSongs = MutableLiveData<List<SongWrapper>>()
    private val _songsMode = MutableLiveData<SongsMode>()
    private val _feedbackMessage = MediatorLiveData<FeedbackMessage>()
    private val _songs = MediatorLiveData<List<SongWrapper>>()
    private val _nowPlayingSong = MediatorLiveData<NowPlayingSongWrapper>()
    private val _queue = MediatorLiveData<List<QueuedSongWrapper>>()
    private val _serverMessage = MediatorLiveData<String>()
    private val _songsLoader = MutableLiveData<Int>(View.VISIBLE)
    private val _queueLoader = MutableLiveData<Int>(View.VISIBLE)
    //publicly immutable
    val songs: LiveData<List<SongWrapper>> = _songs
    val nowPlayingSong: LiveData<NowPlayingSongWrapper> = _nowPlayingSong
    val queue: LiveData<List<QueuedSongWrapper>> = _queue
    val errorMessage: LiveData<String> = _serverMessage
    val filteredSongs: LiveData<List<SongWrapper>> = _filteredSongs
    val songsMode: LiveData<SongsMode> = _songsMode
    val feedbackMessage: LiveData<FeedbackMessage> = _feedbackMessage
    val songsLoader: LiveData<Int> = _songsLoader
    val queueLoader: LiveData<Int> = _queueLoader
    //for user action feedback
    private var userSentSong = false
    private var previouslyQueuedSong: QueuedSongWrapper? = null

    init {
        initFeedbackMediator()
        initDatabaseMediators()
    }

    fun onSongClicked(songName: String){
        userSentSong = true
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

    private fun filterSongs(query: String): List<SongWrapper> {
        return songs.value
            ?.filter { it.name.contains(query, ignoreCase = true) }
            ?.map {
                SongWrapper(it.id, it.name, query,
                    resourceRepo.getColor(R.color.green400_analogous)
                )
            }
            ?.toList() ?: listOf()
    }

    private fun initFeedbackMediator(){
        _feedbackMessage.addSource(queue) { result ->
            val userSong = result?.firstOrNull{ it.ownerId.isUserId() }
            if(!userSentSong){
                if(userSong?.position ?: -1 == (previouslyQueuedSong?.position ?: -1) - 1){
                    //when move up happens, manually update previously queued song position
                    previouslyQueuedSong!!.position -= 1
                }
                if(previouslyQueuedSong == null && userSong != null){
                    //when user queued a song, disconnected and then reconnected
                    previouslyQueuedSong = userSong
                } else if(userSong == null){
                    //when queue changed and user doesnt have a queued song anymore
                    previouslyQueuedSong = null
                }
                return@addSource
            }

            val action = resourceRepo.getString(R.string.view_action)
            val textRes = when {
                previouslyQueuedSong == null -> R.string.queued_feedback
                userSong?.position == previouslyQueuedSong!!.position -> R.string.swapped_feedback
                else -> return@addSource
            }
            _feedbackMessage.value = FeedbackMessage(resourceRepo.getString(textRes), action)
                .also {
                    userSentSong = false
                    previouslyQueuedSong = userSong
                }
        }
        _feedbackMessage.addSource(nowPlayingSong) { result ->
            if(result?.isUserSong == true){
                _feedbackMessage.value = FeedbackMessage(
                    resourceRepo.getString(R.string.user_song_playing_feedback),
                    resourceRepo.getString(R.string.view_action)
                )
            }
        }
    }

    private fun initDatabaseMediators(){
        _songs.addSource(repo.getSongs()) {
            _songs.value = it.map { song ->
                    SongWrapper(song.id!!, song.name, SongWrapper.NO_HIGHLIGHT, SongWrapper.NO_COLOR)
                }
            if(it.isNotEmpty()){
                _songsLoader.value = View.GONE
            }
        }
        _nowPlayingSong.addSource(repo.getNowPlayingSong()) {
            if(it == null) return@addSource
            _nowPlayingSong.value = NowPlayingSongWrapper(
                it.name, it.ownerId, it.ownerId.isUserId()
            )
            _queueLoader.value = View.GONE
        }
        _queue.addSource(repo.getQueuedSongs()) {
            _queue.value = it.map { song ->
                QueuedSongWrapper(song.ownerId, song.name, song.position + 1,
                    if(song.ownerId.isUserId()) View.VISIBLE else View.INVISIBLE)
            }
            if(it.isNotEmpty()){
                _queueLoader.value = View.GONE
            }
        }
        _serverMessage.addSource(repo.getMessage()) {
            _serverMessage.value = it?.message ?: return@addSource
        }
    }

    private fun String.isUserId(): Boolean {
        return this == prefsRepo.get(PREFS_USER_ID, "")
    }
}