package com.dtakac.aux_remote.common.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dtakac.aux_remote.common.model.NowPlayingSong

@Dao
interface NowPlayingSongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setNowPlayingSong(nowPlayingSong: NowPlayingSong)

    @Query("SELECT * FROM now_playing_song_table LIMIT 1")
    fun getNowPlayingSong(): LiveData<NowPlayingSong>

    @Query("DELETE FROM now_playing_song_table")
    suspend fun deleteSong()
}