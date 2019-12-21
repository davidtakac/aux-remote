package com.dtakac.aux_remote.di

import android.content.Context
import androidx.room.Room
import com.dtakac.aux_remote.base.ResourceRepo
import com.dtakac.aux_remote.base.ResourceRepoImpl
import com.dtakac.aux_remote.base.SharedPrefsRepo
import com.dtakac.aux_remote.common.AuxSharedPrefsRepo
import com.dtakac.aux_remote.common.TestSharedPrefsRepo
import com.dtakac.aux_remote.data.AppDatabase
import com.dtakac.aux_remote.network.NetworkUtil
import com.dtakac.aux_remote.network.ClientSocket
import com.dtakac.aux_remote.repository.AuxRepository
import com.dtakac.aux_remote.repository.Repository
import org.koin.dsl.module

val appModule = module {
    single{ ClientSocket() }
    single{ NetworkUtil(get()) }
    single{get<Context>().resources}
    single<ResourceRepo>{ResourceRepoImpl(get())}
    single<Repository>{AuxRepository(get(), get(), get())}
    single{
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "aux-database"
        ).fallbackToDestructiveMigration().build()
    }
    single<SharedPrefsRepo>{// todo: replace with AuxSharedPrefsRepo for production
        TestSharedPrefsRepo(get<Context>().getSharedPreferences("auxprefs", Context.MODE_PRIVATE))
    }
}