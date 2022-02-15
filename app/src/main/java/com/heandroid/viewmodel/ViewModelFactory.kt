package com.heandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.heandroid.network.ApiHelper

class ViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(apiHelper) as T
        } else if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(apiHelper) as T
        } else if (modelClass.isAssignableFrom(RecoveryUsernamePasswordViewModel::class.java)) {
            return RecoveryUsernamePasswordViewModel(apiHelper) as T
        } else if (modelClass.isAssignableFrom(VehicleMgmtViewModel::class.java)) {
            return VehicleMgmtViewModel(apiHelper) as T
        } else if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(apiHelper) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}