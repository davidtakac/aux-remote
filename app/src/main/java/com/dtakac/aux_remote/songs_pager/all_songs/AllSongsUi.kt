package com.dtakac.aux_remote.songs_pager.all_songs

import com.dtakac.aux_remote.data.song.Song

data class AllSongsUi(
    var songs: List<Song>,
    var filteredSongs: List<Song>,
    var isSearching: Boolean
)

fun provideAllSongsUi(songs: List<Song>, filteredSongs: List<Song>, isSearching: Boolean) =
    AllSongsUi(songs, filteredSongs, isSearching)