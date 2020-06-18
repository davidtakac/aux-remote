package com.dtakac.aux_remote.common.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dtakac.aux_remote.common.model.Message

@Dao
interface MessageDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setMessage(message: Message)

    @Query("SELECT * FROM message_table LIMIT 1")
    fun getMessage(): LiveData<Message>
}