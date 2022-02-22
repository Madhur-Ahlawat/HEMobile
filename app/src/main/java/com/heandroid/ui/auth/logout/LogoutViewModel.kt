package com.heandroid.ui.auth.logout

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dummyapplication.data.model.response.LoginResponse
import com.heandroid.R
import com.heandroid.data.model.request.auth.login.LoginModel
import com.heandroid.data.model.response.auth.AuthResponseModel
import com.heandroid.data.repository.auth.LoginRepository
import com.heandroid.data.repository.auth.LogoutRepository
import com.heandroid.ui.base.BaseApplication
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.Resource
import com.heandroid.utils.ResponseHandler.failure
import com.heandroid.utils.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(private val repository: LogoutRepository):BaseViewModel()  {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _logout = MutableLiveData<Resource<AuthResponseModel?>?>()
    val logout : LiveData<Resource<AuthResponseModel?>?> get()  = _logout

    fun logout(){
        viewModelScope.launch {
            try{
                _logout.postValue(success(repository.logout(),errorManager))
            }catch (e: java.lang.Exception){
                _logout.postValue(failure(e))
            }
        }
    }
}