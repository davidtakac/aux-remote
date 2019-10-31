package com.dtakac.aux_remote.data.now_playing_song

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Observable

@Dao
interface NowPlayingSongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setNowPlayingSong(nowPlayingSong: NowPlayingSong)

    @Query("SELECT * FROM now_playing_song_table LIMIT 1")
    fun getNowPlayingSong(): Observable<NowPlayingSong>
}