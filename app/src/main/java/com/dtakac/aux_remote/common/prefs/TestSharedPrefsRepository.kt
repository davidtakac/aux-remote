package com.dtakac.aux_remote.common.prefs

import android.content.SharedPreferences
import com.dtakac.aux_remote.base.prefs.SharedPrefsRepository
import com.dtakac.aux_remote.base.prefs.SharedPrefsUtil
import com.dtakac.aux_remote.common.constants.PREFS_USER_ID

private const val CLIENT_ID = "123456789"
class TestSharedPrefsRepository(private val prefs: SharedPreferences):
    SharedPrefsRepository {
    override fun save(key: String, value: String) = SharedPrefsUtil.save(prefs, key, value)

    override fun get(key: String, defaultValue: String) =
        if(key == PREFS_USER_ID) CLIENT_ID
        else SharedPrefsUtil.get(prefs, key, defaultValue)
}