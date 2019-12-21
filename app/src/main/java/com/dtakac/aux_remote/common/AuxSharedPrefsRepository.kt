package com.dtakac.aux_remote.common

import android.content.SharedPreferences
import com.dtakac.aux_remote.base.SharedPrefsRepository
import com.dtakac.aux_remote.base.SharedPrefsUtil

class AuxSharedPrefsRepository(private val prefs: SharedPreferences): SharedPrefsRepository{
    override fun save(key: String, value: String) = SharedPrefsUtil.save(prefs, key, value)
    override fun get(key: String, defaultValue: String) = SharedPrefsUtil.get(prefs, key, defaultValue)
}