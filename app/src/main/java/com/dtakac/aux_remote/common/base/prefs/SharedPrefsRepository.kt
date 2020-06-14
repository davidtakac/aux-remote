package com.dtakac.aux_remote.common.base.prefs
import android.content.SharedPreferences

interface SharedPrefsRepository {
    fun get(key: String, defaultValue: String): String
    fun save(key: String, value: String)
}

object SharedPrefsUtil {
    fun save(prefs: SharedPreferences, key: String, value: String) {
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    operator fun get(prefs: SharedPreferences, key: String, defaultValue: String): String =
        prefs.getString(key, defaultValue) ?: defaultValue

    fun save(prefs: SharedPreferences, key: String, value: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    operator fun get(prefs: SharedPreferences, key: String, defaultValue: Boolean): Boolean =
        prefs.getBoolean(key, defaultValue)
}