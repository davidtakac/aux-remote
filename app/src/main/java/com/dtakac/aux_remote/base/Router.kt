package com.dtakac.aux_remote.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

interface Router {
    fun showFragment(fragmentManager: FragmentManager?, bundle: Bundle, tag: String, container: Int = android.R.id.content)
    fun popBackStack(fragmentManager: FragmentManager?)
}

inline fun <reified T : Fragment> displayFragment(fragmentManager: FragmentManager?, bundle: Bundle, tag: String, container: Int = android.R.id.content) {
    fragmentManager?.let {

        val fragment = T::class.java.newInstance().apply {
            arguments = bundle
        }

        val transaction = it.beginTransaction()
            .replace(container, fragment, tag)

        if (container == android.R.id.content) {
            transaction.addToBackStack(tag)
        }

        transaction.setReorderingAllowed(true)
        transaction.commitAllowingStateLoss()
    }
}

inline fun <reified T : Fragment> newFragmentInstance(bundle: Bundle) =
    T::class.java.newInstance().apply {
        arguments = bundle
    }

