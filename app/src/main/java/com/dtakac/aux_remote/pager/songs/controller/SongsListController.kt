package com.dtakac.aux_remote.pager.songs.controller

import com.airbnb.epoxy.EpoxyController
import com.dtakac.aux_remote.pager.songs.mode.SongsMode
import com.dtakac.aux_remote.pager.songs.view_holders.song
import com.dtakac.aux_remote.pager.songs.wrapper.SongWrapper

class SongsListController(private val songsListInterface: SongsListInterface): EpoxyController(){
    private var songs: List<SongWrapper>? = listOf()
    private var filteredSongs: List<SongWrapper>? = listOf()
    private var mode = SongsMode.SONGS

    fun setSongs(songs: List<SongWrapper>?){
        this.songs = songs
        requestModelBuild()
    }

    fun setFilteredSongs(songs: List<SongWrapper>?){
        this.filteredSongs = songs
        requestModelBuild()
    }

    fun setMode(mode: SongsMode){
        this.mode = mode
        requestModelBuild()
    }

    override fun buildModels() {
        val data = when(mode){
            SongsMode.SONGS -> songs
            SongsMode.FILTERED_SONGS -> filteredSongs
        }
        data?.forEach {
            song {
                id(it.id)
                name(it.highlightedName)
                clickListener { _, _, _, _ -> songsListInterface.onSongClicked(it.name) }
            }
        }
    }
}