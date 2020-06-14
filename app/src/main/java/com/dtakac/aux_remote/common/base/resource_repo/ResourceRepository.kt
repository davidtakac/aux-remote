package com.dtakac.aux_remote.common.base.resource_repo

interface ResourceRepository {
    fun getString(resId: Int): String
    fun getColor(resId: Int): Int
}