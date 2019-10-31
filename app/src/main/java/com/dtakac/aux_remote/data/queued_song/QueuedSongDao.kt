package com.dtakac.aux_remote.data.queued_song

import androidx.room.*
import io.reactivex.Observable

@Dao
interface QueuedSongDao {
    @Query("SELECT * FROM queued_song_table ORDER BY timestamp ASC")
    fun getQueuedSongsOldestFirst(): Observable<List<QueuedSong>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(queuedSong: QueuedSong)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllOrUpdate(queuedSongs: List<QueuedSong>)

    @Query("DELETE FROM queued_song_table WHERE timestamp = (SELECT MAX(timestamp) FROM queued_song_table)")
    fun deleteOldest()

    @Query("DELETE FROM queued_song_table")
    fun deleteAll()
}