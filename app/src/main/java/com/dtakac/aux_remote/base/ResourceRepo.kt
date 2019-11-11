package com.dtakac.aux_remote.base

interface ResourceRepo {
    fun getString(resId: Int): String
    fun getColor(resId: Int): Int
}