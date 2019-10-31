package com.dtakac.aux_remote.di

import android.content.Context
import androidx.room.Room
import com.dtakac.aux_remote.base.SharedPrefsRepo
import com.dtakac.aux_remote.common.AuxSharedPrefsRepo
import com.dtakac.aux_remote.data.AppDatabase
import com.dtakac.aux_remote.network.NetworkUtil
import com.dtakac.aux_remote.network.ClientSocket
import org.koin.dsl.module

val appModule = module {
    single{ ClientSocket() }

    single<SharedPrefsRepo>{
        AuxSharedPrefsRepo(get<Context>().getSharedPreferences("auxprefs", Context.MODE_PRIVATE))
    }

    single{ NetworkUtil(get()) }

    single{
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "aux-database"
        ).fallbackToDestructiveMigration().build()
    }
}