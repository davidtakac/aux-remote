package com.dtakac.aux_remote.common.di

import android.content.Context
import androidx.room.Room
import com.dtakac.aux_remote.base.resource_repo.ResourceRepository
import com.dtakac.aux_remote.base.resource_repo.ResourceRepoImpl
import com.dtakac.aux_remote.base.prefs.SharedPrefsRepository
import com.dtakac.aux_remote.common.database.AppDatabase
import com.dtakac.aux_remote.common.network.NetworkUtil
import com.dtakac.aux_remote.common.network.ClientSocket
import com.dtakac.aux_remote.common.database_repository.AuxDatabaseRepository
import com.dtakac.aux_remote.common.database_repository.DatabaseRepository
import com.dtakac.aux_remote.common.prefs.AuxSharedPrefsRepository
import org.koin.dsl.module

val appModule = module {
    single{ ClientSocket() }
    single{ NetworkUtil(get()) }
    single{get<Context>().resources}
    single<ResourceRepository>{ ResourceRepoImpl(get()) }
    single<DatabaseRepository>{AuxDatabaseRepository(get(), get(), get(), get(), get())}
    single{
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "aux-database"
        ).fallbackToDestructiveMigration().build()
    }
    single<SharedPrefsRepository>{
        AuxSharedPrefsRepository(
            get<Context>().getSharedPreferences(
                "auxprefs",
                Context.MODE_PRIVATE
            )
        )
    }
}