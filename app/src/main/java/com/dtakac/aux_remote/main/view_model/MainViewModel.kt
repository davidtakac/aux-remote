package com.dtakac.aux_remote.main.view_model

import androidx.lifecycle.ViewModel
import com.dtakac.aux_remote.common.repository.Repository

class MainViewModel(
    private val repo: Repository
): ViewModel(){
    fun getMessage() = repo.getMessage()
}