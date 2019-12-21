package com.dtakac.aux_remote.base

interface ResourceRepository {
    fun getString(resId: Int): String
    fun getColor(resId: Int): Int
}