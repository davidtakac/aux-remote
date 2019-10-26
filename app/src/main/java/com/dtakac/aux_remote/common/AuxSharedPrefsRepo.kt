package com.dtakac.aux_remote.common

import android.content.SharedPreferences
import com.dtakac.aux_remote.base.SharedPrefsRepo
import com.dtakac.aux_remote.base.SharedPrefsUtil

class AuxSharedPrefsRepo(
    private val prefs: SharedPreferences
) : SharedPrefsRepo{

    override fun save(key: String, value: String) =
        SharedPrefsUtil.save(prefs, key, value)

    override fun get(key: String, defaultValue: String): String =
        SharedPrefsUtil.get(prefs, key, defaultValue)

}