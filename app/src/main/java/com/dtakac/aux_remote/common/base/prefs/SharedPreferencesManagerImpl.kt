package com.dtakac.aux_remote.common.base.prefs

import android.content.SharedPreferences

class SharedPreferencesManagerImpl(private val prefs: SharedPreferences): SharedPreferencesManager {
    override fun save(key: String, value: String) {
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }
    override fun get(key: String, defaultValue: String): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }
}