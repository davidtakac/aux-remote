package com.dtakac.aux_remote.common.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dtakac.aux_remote.common.model.Nickname

@Dao
interface NicknameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(nickname: Nickname)

    @Query("SELECT * FROM nickname_table WHERE ownerId = :ownerId")
    suspend fun getNickname(ownerId: String): Nickname?
}