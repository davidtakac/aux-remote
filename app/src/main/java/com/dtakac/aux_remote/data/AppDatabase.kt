package com.dtakac.aux_remote.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Song::class], version = 4)
abstract class AppDatabase: RoomDatabase(){
    abstract fun songDao(): SongDao
}