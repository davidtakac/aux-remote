package com.dtakac.aux_remote.common.base.resource

import android.content.res.Resources

class AndroidResourceRepository(
    private val resources: Resources
) : ResourceRepository {
    override fun getString(resId: Int): String =
        resources.getString(resId)

    override fun getColor(resId: Int): Int =
        resources.getColor(resId)
}