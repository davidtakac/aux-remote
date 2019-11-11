package com.dtakac.aux_remote.base

import android.content.res.Resources

class ResourceRepoImpl(
    private val resources: Resources
) : ResourceRepo{
    override fun getString(resId: Int): String =
        resources.getString(resId)

    override fun getColor(resId: Int): Int =
        resources.getColor(resId)
}