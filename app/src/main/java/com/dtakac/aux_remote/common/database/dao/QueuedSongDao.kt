package com.dtakac.aux_remote.common.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dtakac.aux_remote.common.model.QueuedSong

@Dao
interface QueuedSongDao {
    @Query("SELECT * FROM queued_song_table ORDER BY position ASC")
    fun getQueuedSongs(): LiveData<List<QueuedSong>>

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