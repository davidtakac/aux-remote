package com.dtakac.aux_remote.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject

abstract class BaseFragment : Fragment(){

    protected val router by inject<Router>()
    private val compositeDisposable = CompositeDisposable()
    abstract val layoutRes: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutRes, container, false)

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    protected fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)
}