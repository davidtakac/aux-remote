package com.dtakac.aux_remote.base
import android.content.SharedPreferences

interface SharedPrefsRepo {
    fun get(key: String, defaultValue: String): String
    fun save(key: String, value: String)
}

object SharedPrefsUtil {
    fun save(prefs: SharedPreferences, key: String, value: String) {
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    operator fun get(prefs: SharedPreferences, key: String, defaultValue: String): String? =
        try {
            prefs.getString(key, defaultValue)
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }

    fun save(prefs: SharedPreferences, key: String, value: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    operator fun get(prefs: SharedPreferences, key: String, defaultValue: Boolean): Boolean =
        try {
            prefs.getBoolean(key, defaultValue)
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
}