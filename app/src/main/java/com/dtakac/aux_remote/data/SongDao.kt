package com.dtakac.aux_remote.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Observable

@Dao
interface SongDao {
    @Insert
    fun insert(song: Song)

    @Insert
    fun insertAll(vararg songs: Song)

    @Query("SELECT * FROM song_table")
    fun getAll(): Observable<List<Song>>

    @Query("DELETE FROM song_table")
    fun deleteAll()

    @Query("SELECT * FROM song_table WHERE id=:songId")
    fun get(songId: Int): Song?
}