package com.heandroid.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.heandroid.utils.AppRepository

class ViewModelProviderFactory(
    val app: Application,
    val appRepository: AppRepository,
) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(app, appRepository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}