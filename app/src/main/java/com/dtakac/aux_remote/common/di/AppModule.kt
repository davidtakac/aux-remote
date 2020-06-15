package com.dtakac.aux_remote.common.di

import android.content.Context
import androidx.room.Room
import com.dtakac.aux_remote.common.base.resource.ResourceRepository
import com.dtakac.aux_remote.common.base.resource.AndroidResourceRepository
import com.dtakac.aux_remote.common.base.prefs.SharedPrefsRepository
import com.dtakac.aux_remote.common.database.AppDatabase
import com.dtakac.aux_remote.common.network.NetworkUtil
import com.dtakac.aux_remote.common.network.ClientSocket
import com.dtakac.aux_remote.common.repository.AuxRepository
import com.dtakac.aux_remote.common.repository.Repository
import com.dtakac.aux_remote.common.base.prefs.AndroidSharedPrefsRepository
import org.koin.dsl.module

val appModule = module {
    single{ ClientSocket() }
    single{ NetworkUtil(get()) }
    single{ get<Context>().resources }
    single<ResourceRepository>{ AndroidResourceRepository(get()) }
    single<Repository>{ AuxRepository(get(), get()) }
    single{
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "aux-database"
        ).fallbackToDestructiveMigration().build()
    }
    single<SharedPrefsRepository>{
        AndroidSharedPrefsRepository(
            get<Context>().getSharedPreferences(
                "auxprefs",
                Context.MODE_PRIVATE
            )
        )
    }
}