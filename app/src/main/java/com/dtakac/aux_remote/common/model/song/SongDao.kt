package com.dtakac.aux_remote.common.model.song

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Observable

@Dao
interface SongDao {
    @Insert
    fun insert(song: Song)

    @Insert
    fun insertAll(songs: List<Song>)

    @Query("SELECT * FROM song_table")
    fun getAll(): Observable<List<Song>>

    @Query("DELETE FROM song_table")
    fun deleteAll()

    @Query("SELECT * FROM song_table WHERE id=:songId")
    fun get(songId: Int): Song?
}