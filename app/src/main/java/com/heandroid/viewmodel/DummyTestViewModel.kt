package com.heandroid.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.model.AccountResponse
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import com.heandroid.utils.LoginDataState
import com.heandroid.utils.UtilityClass
import com.heandroid.utils.Utils.isEmailValid
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class DummyTestViewModel(private val apiHelper: ApiHelper):ViewModel() {

    val uiState = MutableLiveData<LoginDataState>()

     fun authenticate(clientID:String,
                      grantType:String,
                      agencyId:String,
                      clientSecret:String,
                      value:String,
                      password:String,
                      validatePasswordCompliance:String) {
             viewModelScope.launch {
                 runCatching {
                     apiHelper.loginApiCall(clientID, grantType, agencyId , clientSecret , value ,password , validatePasswordCompliance)
                 }.onSuccess {
                     uiState.postValue(LoginDataState.Success(it))
                 }.onFailure {
                     uiState.postValue(LoginDataState.Error("Request Failed,Please try later."))
                 }
         }
    }


    private fun areUserCredentialsValid(userEmail: String, password: String): Boolean {
        return if (!UtilityClass.isEmailValid(userEmail)) {
            uiState.postValue(LoginDataState.InValidEmailState)
            false
        } else if (!UtilityClass.isPasswordValid(password)) {
            uiState.postValue(LoginDataState.InValidPasswordState)
            false
        } else {
            uiState.postValue(LoginDataState.ValidCredentialsState)
            true
        }
    }

    fun getObserverState() = uiState
}