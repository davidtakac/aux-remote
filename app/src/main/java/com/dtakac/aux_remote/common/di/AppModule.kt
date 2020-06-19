package com.dtakac.aux_remote.common.di

import android.content.Context
import androidx.room.Room
import com.dtakac.aux_remote.common.base.resource.ResourceRepository
import com.dtakac.aux_remote.common.base.resource.AndroidResourceRepository
import com.dtakac.aux_remote.common.base.prefs.SharedPreferencesManager
import com.dtakac.aux_remote.common.database.AppDatabase
import com.dtakac.aux_remote.common.util.NetworkUtil
import com.dtakac.aux_remote.server.ServerSocket
import com.dtakac.aux_remote.common.repository.AuxRepository
import com.dtakac.aux_remote.common.repository.Repository
import com.dtakac.aux_remote.common.base.prefs.SharedPreferencesManagerImpl
import com.dtakac.aux_remote.common.prefs.AuxSharedPrefsRepository
import com.dtakac.aux_remote.common.prefs.AuxSharedPrefsRepositoryImpl
import com.dtakac.aux_remote.server.AuxServerInteractor
import com.dtakac.aux_remote.server.ServerInteractor
import org.koin.dsl.module

val appModule = module {
    single{ ServerSocket() }
    single<ServerInteractor> { AuxServerInteractor(get(), get()) }
    single{ NetworkUtil(get()) }
    single{ get<Context>().resources }
    single<ResourceRepository>{ AndroidResourceRepository(get()) }
    single<Repository>{ AuxRepository(get()) }
    single{
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "aux-database"
        ).fallbackToDestructiveMigration().build()
    }
    single<SharedPreferencesManager>{
        SharedPreferencesManagerImpl(
            get<Context>().getSharedPreferences(
                "auxprefs",
                Context.MODE_PRIVATE
            )
        )
    }
    single<AuxSharedPrefsRepository>{ AuxSharedPrefsRepositoryImpl(get()) }
}