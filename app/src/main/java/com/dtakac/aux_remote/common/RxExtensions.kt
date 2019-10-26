package com.dtakac.aux_remote.common

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.defaultSchedulers() = this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())