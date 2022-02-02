package com.heandroid.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.heandroid.model.LoginResponse
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private val apiHelper: ApiHelper) : ViewModel(), Observable {

    // todo getter setter method
    val loginUserVal = MutableLiveData<Resource<Response<LoginResponse>>>()
    val renewalUserLoginVal = MutableLiveData<Resource<Response<LoginResponse>>>()


    // changes for data binding
    val isStringEmpty = MutableLiveData<Boolean>()

    @Bindable
    val inputUserName = MutableLiveData<String>()
    @Bindable
    val inputPassword = MutableLiveData<String>()

    init {
        isStringEmpty.value = false
    }

    fun loginUser(
        clientID: String,
        grantType: String,
        agencyId: String,
        clientSecret: String,
        value: String,
        password: String,
        validatePasswordCompliance: String
    ) {

        viewModelScope.launch {
            loginUserVal.postValue(Resource.loading(null))
            try {
                //todo reduce parameter
                val usersFromApi = apiHelper.loginApiCall(clientID,
                    grantType,
                    agencyId,
                    clientSecret,
                    value,
                    password,
                    validatePasswordCompliance)
                //loginUserVal.postValue(Resource.success(usersFromApi))
                loginUserVal.postValue(setLoginUserResponse(usersFromApi))
            } catch (e: Exception) {
                loginUserVal.postValue(Resource.error(null , e.toString()))
            }
        }
    }

    private fun setLoginUserResponse(usersFromApi: Response<LoginResponse>): Resource<Response<LoginResponse>>? {
        if(usersFromApi.isSuccessful)
        {
            return Resource.success(usersFromApi)
        }
        else
        {
            var errorCode = usersFromApi.code()
            return if(errorCode==401) {
                Resource.error(null, "Invalid login credentials")
            } else {
                Resource.error(null, "Unknown error ")
            }

        }
    }


    fun getRenewalAccessToken(
        clientId: String, grantType: String, agencyId: String, clientSecret: String,
        refreshToken: String, validatePasswordCompliance: String
    ) {
//        = liveData(Dispatchers.IO) {
//            emit(Resource.loading(data = null))
//            try {
//                emit(
//                    Resource.success(
//                        data = apiHelper.getRenewalAccessToken(
//                            clientId,
//                            grantType,
//                            agencyId,
//                            clientSecret,
//                            refreshToken!!,
//                            validatePasswordCompliance
//                        )
//                    )
//                )
//            } catch (exception: Exception) {
//                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
//            }
//        }

        viewModelScope.launch {
            renewalUserLoginVal.postValue(Resource.loading(null))
            try {
                val usersFromApi = apiHelper.getRenewalAccessToken(
                    clientId,
                          grantType,
                            agencyId,
                            clientSecret,
                    refreshToken!!, validatePasswordCompliance)
                //loginUserVal.postValue(Resource.success(usersFromApi))
                renewalUserLoginVal.postValue(setLoginUserResponse(usersFromApi))
            } catch (e: Exception) {
                renewalUserLoginVal.postValue(Resource.error(null , e.toString()))
            }
        }
    }

    // todo remove these extensions
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {


    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }


}