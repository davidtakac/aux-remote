package com.dtakac.aux_remote.common.base.prefs

import android.content.SharedPreferences
import com.dtakac.aux_remote.common.util.SharedPrefsUtil

class AndroidSharedPrefsRepository(private val prefs: SharedPreferences): SharedPrefsRepository {
    override fun save(key: String, value: String) = SharedPrefsUtil.save(prefs, key, value)
    override fun get(key: String, defaultValue: String) = SharedPrefsUtil.get(prefs, key, defaultValue)
}