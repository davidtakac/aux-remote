package com.dtakac.aux_remote.common.model.message

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Observable

@Dao
interface MessageDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setMessage(message: Message)

    @Query("SELECT * FROM message_table LIMIT 1")
    fun getMessage(): Observable<Message>
}