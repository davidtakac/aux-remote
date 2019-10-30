package com.dtakac.aux_remote.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Song::class), version = 3)
abstract class AppDatabase: RoomDatabase(){
    abstract fun songDao(): SongDao
}