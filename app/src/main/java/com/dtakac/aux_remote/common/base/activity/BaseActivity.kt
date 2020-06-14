package com.dtakac.aux_remote.common.base.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

// from io.reactivex.rxkotlin.subscribers.kt
private val onNextStub: (Any) -> Unit = {}
private val onErrorStub: (Throwable) -> Unit = {}
private val onCompleteStub: () -> Unit = {}

abstract class BaseActivity : AppCompatActivity(){
    private val compositeDisposable = CompositeDisposable()
    abstract val layoutRes: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
        onCreated()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    /**
    Subscribes and adds disposable to composite disposable.
     */
    protected fun <T: Any> Observable<T>.subscribeByAndDispose(
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onNext: (T) -> Unit = onNextStub
    ) = addDisposable(subscribeBy(onError = onError, onNext = onNext, onComplete = onComplete))

    protected fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    protected open fun onCreated(){}
}