package com.dtakac.aux_remote.common

import android.content.SharedPreferences
import com.dtakac.aux_remote.base.SharedPrefsRepo

class AuxSharedPrefsRepo(
    private val prefs: SharedPreferences
) : SharedPrefsRepo{

    override fun save(key: String, value: String) {

    }

    override fun get(key: String, defaultValue: String): String {
        return ""
    }

}