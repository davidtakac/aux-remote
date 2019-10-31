package com.dtakac.aux_remote.data.queued_song

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Observable

@Dao
interface QueuedSongDao {
    @Query("SELECT * FROM queued_song_table ORDER BY timestamp DESC")
    fun getQueuedSongsOldestFirst(): Observable<List<QueuedSong>>

    @Insert
    fun insert(queuedSong: QueuedSong)

    @Insert
    fun insertAll(vararg queuedSong: QueuedSong)

    @Query("DELETE FROM queued_song_table WHERE timestamp = (SELECT MAX(timestamp) FROM queued_song_table)")
    fun deleteOldest()

    @Query("DELETE FROM queued_song_table")
    fun deleteAll()
}