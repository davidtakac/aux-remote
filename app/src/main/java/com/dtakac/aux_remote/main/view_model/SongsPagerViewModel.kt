package com.dtakac.aux_remote.main.view_model

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.resource.ResourceRepository
import com.dtakac.aux_remote.common.base.prefs.SharedPrefsRepository
import com.dtakac.aux_remote.common.repository.DatabaseRepository
import com.dtakac.aux_remote.main.songs.wrapper.SongWrapper
import com.dtakac.aux_remote.common.constants.*
import com.dtakac.aux_remote.main.common.FeedbackMessage
import com.dtakac.aux_remote.main.common.SongsMode
import com.dtakac.aux_remote.main.queue.wrapper.NowPlayingSongWrapper
import com.dtakac.aux_remote.main.queue.wrapper.QueuedSongWrapper
import com.dtakac.aux_remote.server.ServerInteractor
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "meteor_server"
class SongsPagerViewModel(
    private val repo: DatabaseRepository,
    private val prefsRepo: SharedPrefsRepository,
    private val serverInteractor: ServerInteractor,
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
        initRepositoryMediators()
        if(serverInteractor.initializeReaderAndWriter()){
            viewModelScope.launch { listenToServer() }
        } else {
            viewModelScope.launch { onListenStopped("Couldn't initialize reader or writer.") }
        }
    }

    fun onSongClicked(songName: String){
        userSentSong = true
        viewModelScope.launch {
            serverInteractor.writeSongToServer(prefsRepo.get(PREFS_USER_ID, ""), songName)
        }
    }

    fun onQueryTextChanged(query: String) {
        viewModelScope.launch {
            _filteredSongs.value = filterSongs(query)
            _songsMode.value = SongsMode.FILTERED_SONGS
        }
    }

    fun onSearchCollapsed(){
        _songsMode.value = SongsMode.SONGS
    }

    fun pullFromServer() {
        viewModelScope.launch {
            serverInteractor.writeLineToServer(CLIENT_REQUEST_SONGS)
            serverInteractor.writeLineToServer(CLIENT_REQUEST_QUEUE)
            serverInteractor.writeLineToServer(CLIENT_REQUEST_PLAYING)
        }
    }

    private suspend fun filterSongs(query: String): List<SongWrapper> {
        var filtered = listOf<SongWrapper>()
        withContext(Default) {
            filtered = songs.value
                ?.filter { it.name.contains(query, ignoreCase = true) }
                ?.map {
                    SongWrapper(
                        it.id, it.name, query,
                        resourceRepo.getColor(R.color.green400_analogous)
                    )
                }
                ?.toList() ?: listOf()
        }
        return filtered
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
            userSentSong = false
            previouslyQueuedSong = userSong
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

    private fun initRepositoryMediators(){
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

    private suspend fun listenToServer() {
        var stopMessage: String? = null
        withContext(IO){
            while (true) {
                try {
                    handleServerData(serverInteractor.getNextData())
                } catch (e: Exception) {
                    stopMessage = e.message
                    Log.e(TAG, "Exception when listening to server, stopping. Message: ${e.message}")
                    break
                }
            }
        }
        onListenStopped(stopMessage)
    }

    private suspend fun handleServerData(lines: List<String>){
        if(lines.isNotEmpty()) {
            val body = lines.subList(1, lines.size)
            when (lines[0]) {
                SERVER_SONG_LIST -> repo.insertSongs(body)
                SERVER_QUEUE_LIST -> repo.insertQueuedSongs(body)
                SERVER_ENQUEUED -> repo.insertQueuedSong(body)
                SERVER_MOVE_UP -> repo.moveUp()
                SERVER_NOW_PLAYING -> repo.updateNowPlayingSong(body)
            }
        }
    }

    private suspend fun onListenStopped(messageText: String?){
        repo.updateMessage(messageText ?: EMPTY_STRING)
    }
}