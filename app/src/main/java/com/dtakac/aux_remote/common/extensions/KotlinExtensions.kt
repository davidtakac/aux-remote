package com.dtakac.aux_remote.common.extensions

import androidx.lifecycle.MutableLiveData
import com.dtakac.aux_remote.common.dao.QueuedSongDao
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.defaultSchedulers() =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> MutableLiveData<T>.forceRefresh() {
    this.value = this.value
}