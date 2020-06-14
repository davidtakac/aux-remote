package com.dtakac.aux_remote.main.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtakac.aux_remote.common.database_repository.DatabaseRepository

class MainViewModel(
    private val repo: DatabaseRepository
): ViewModel(){
    val messageLiveData = MutableLiveData<String>()

    fun getMessage() = repo.getMessage().doOnNext { messageLiveData.value = it }
}