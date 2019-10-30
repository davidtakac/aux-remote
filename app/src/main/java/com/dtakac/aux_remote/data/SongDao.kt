package com.dtakac.aux_remote.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SongDao {
    @Insert
    fun insert(song: Song)

    @Insert
    fun insertAll(vararg songs: Song)

    @Query("SELECT * FROM song_table")
    fun getAll(): LiveData<List<Song>>

    @Query("DELETE FROM song_table")
    fun deleteAll()
}