package com.dtakac.aux_remote.common.base.prefs

interface SharedPrefsRepository {
    fun get(key: String, defaultValue: String): String
    fun save(key: String, value: String)
}