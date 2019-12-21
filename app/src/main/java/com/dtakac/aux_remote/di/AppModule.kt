package com.dtakac.aux_remote.di

import android.content.Context
import androidx.room.Room
import com.dtakac.aux_remote.base.ResourceRepository
import com.dtakac.aux_remote.base.ResourceRepoImpl
import com.dtakac.aux_remote.base.SharedPrefsRepository
import com.dtakac.aux_remote.common.TestSharedPrefsRepository
import com.dtakac.aux_remote.data.AppDatabase
import com.dtakac.aux_remote.network.NetworkUtil
import com.dtakac.aux_remote.network.ClientSocket
import com.dtakac.aux_remote.repository.AuxDatabaseRepository
import com.dtakac.aux_remote.repository.DatabaseRepository
import org.koin.dsl.module

val appModule = module {
    single{ ClientSocket() }
    single{ NetworkUtil(get()) }
    single{get<Context>().resources}
    single<ResourceRepository>{ResourceRepoImpl(get())}
    single<DatabaseRepository>{AuxDatabaseRepository(get(), get(), get(), get())}
    single{
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "aux-database"
        ).fallbackToDestructiveMigration().build()
    }
    single<SharedPrefsRepository>{// todo: replace with AuxSharedPrefsRepository for production
        TestSharedPrefsRepository(get<Context>().getSharedPreferences("auxprefs", Context.MODE_PRIVATE))
    }
}