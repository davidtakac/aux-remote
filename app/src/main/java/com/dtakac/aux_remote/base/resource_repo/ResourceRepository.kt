package com.dtakac.aux_remote.base.resource_repo

interface ResourceRepository {
    fun getString(resId: Int): String
    fun getColor(resId: Int): Int
}