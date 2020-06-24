package com.dtakac.aux_remote.common.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dtakac.aux_remote.common.model.Song

@Dao
interface SongDao {
    @Insert
    suspend fun insertAll(songs: List<Song>)

    @Query("SELECT * FROM song_table")
    fun getAll(): LiveData<List<Song>>

    @Query("DELETE FROM song_table")
    suspend fun deleteAll()
}