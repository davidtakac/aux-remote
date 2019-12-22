package com.dtakac.aux_remote.base.resource_repo

import android.content.res.Resources

class ResourceRepoImpl(
    private val resources: Resources
) : ResourceRepository {
    override fun getString(resId: Int): String =
        resources.getString(resId)

    override fun getColor(resId: Int): Int =
        resources.getColor(resId)
}