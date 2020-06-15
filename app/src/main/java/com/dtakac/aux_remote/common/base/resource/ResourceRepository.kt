package com.dtakac.aux_remote.common.base.resource

interface ResourceRepository {
    fun getString(resId: Int): String
    fun getColor(resId: Int): Int
}