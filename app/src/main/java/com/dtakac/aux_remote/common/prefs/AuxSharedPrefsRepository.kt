package com.dtakac.aux_remote.common.prefs

import android.content.SharedPreferences
import com.dtakac.aux_remote.base.prefs.SharedPrefsRepository
import com.dtakac.aux_remote.base.prefs.SharedPrefsUtil

class AuxSharedPrefsRepository(private val prefs: SharedPreferences):
    SharedPrefsRepository {
    override fun save(key: String, value: String) = SharedPrefsUtil.save(prefs, key, value)
    override fun get(key: String, defaultValue: String) = SharedPrefsUtil.get(prefs, key, defaultValue)
}