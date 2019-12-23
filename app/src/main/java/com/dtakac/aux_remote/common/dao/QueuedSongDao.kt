package com.dtakac.aux_remote.common.dao

import androidx.room.*
import com.dtakac.aux_remote.common.model.QueuedSong
import io.reactivex.Observable

@Dao
interface QueuedSongDao {
    @Query("SELECT * FROM queued_song_table ORDER BY position ASC")
    fun getQueuedSongs(): Observable<List<QueuedSong>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(queuedSong: QueuedSong)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllOrUpdate(queuedSongs: List<QueuedSong>)

    @Query("DELETE FROM queued_song_table WHERE position = (SELECT MIN(position) FROM queued_song_table)")
    fun deleteFirst()

    @Query("DELETE FROM queued_song_table")
    fun deleteAll()

    @Query("UPDATE queued_song_table SET position = position - 1")
    fun decrementPosition()
}