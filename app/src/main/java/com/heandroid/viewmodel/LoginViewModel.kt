package com.heandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.heandroid.network.ApiHelper
import com.heandroid.repo.AppRepository
import com.heandroid.repo.Resource
import kotlinx.coroutines.Dispatchers
//
//class LoginViewModel(private val appRepository: AppRepository):ViewModel() {
class LoginViewModel(private val apiHelper: ApiHelper):ViewModel() {

    fun loginUser(clientID:String,
                  grantType:String,
                  agencyId:String,
                  clientSecret:String,
                  value:String,
                  password:String,
                  validatePasswordCompliance:String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiHelper.loginApiCall(clientID, grantType, agencyId, clientSecret, value, password, validatePasswordCompliance)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getRenewalAccessToken(clientId:String , grantType:String, agencyId:String, clientSecret:String,
                              refreshToken:String, validatePasswordCompliance:String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiHelper.getRenewalAccessToken(clientId , grantType, agencyId, clientSecret, refreshToken!!, validatePasswordCompliance)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

}