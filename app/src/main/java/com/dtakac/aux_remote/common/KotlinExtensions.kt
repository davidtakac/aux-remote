package com.dtakac.aux_remote.common

import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.defaultSchedulers() = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> MutableLiveData<T>.update() {
    this.value = this.value
}