package com.dtakac.aux_remote.common

import android.content.SharedPreferences
import com.dtakac.aux_remote.base.SharedPrefsRepo
import com.dtakac.aux_remote.base.SharedPrefsUtil

private const val CLIENT_ID = "123456789"
class TestSharedPrefsRepo(
    private val prefs: SharedPreferences
) : SharedPrefsRepo {

    override fun save(key: String, value: String) =
        SharedPrefsUtil.save(prefs, key, value)

    override fun get(key: String, defaultValue: String): String =
        if(key == PREFS_USER_ID) CLIENT_ID
        else SharedPrefsUtil.get(prefs, key, defaultValue)
}