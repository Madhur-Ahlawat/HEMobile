package com.heandroid.ui.viewcharges

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.R
import com.heandroid.data.model.auth.forgot.email.LoginModel
import com.heandroid.data.model.auth.login.LoginResponse
import com.heandroid.data.model.tollrates.TollRatesResp
import com.heandroid.data.repository.auth.LoginRepository
import com.heandroid.data.repository.viewcharge.ViewChargeRepository
import com.heandroid.ui.base.BaseApplication
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler.failure
import com.heandroid.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewChargeViewModel @Inject constructor(private val repository: ViewChargeRepository):BaseViewModel()  {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _tollRates = MutableLiveData<Resource<List<TollRatesResp>?>?>()
    val tollRates : LiveData<Resource<List<TollRatesResp>?>?> get()  = _tollRates

    fun tollRates() {
        viewModelScope.launch {
            try {
                _tollRates.postValue(success(repository.tollRates(),errorManager))
            } catch (e: Exception) {
                _tollRates.postValue(failure(e))
            }
            }
        }
}