package com.heandroid.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.R
import com.heandroid.config.MyApplication
import com.heandroid.model.LoginResponse
import com.heandroid.utils.AppRepository
import com.heandroid.utils.Event
import com.heandroid.utils.Resource
import com.heandroid.utils.Utils
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

class LoginViewModel(
    application: Application,
    private val appRepository: AppRepository,
) :
    AndroidViewModel(application) {

    private val _loginResponse = MutableLiveData<Event<Resource<LoginResponse>>>()
    val loginResponse: LiveData<Event<Resource<LoginResponse>>> = _loginResponse

    fun loginUser(clientID :String, grantType:String, agecyId:String,
                  clientSecret:String, value:String, password:String, validatePasswordCompliance:String) =
        viewModelScope.launch {
        login(clientID, grantType, agecyId, clientSecret, value, password, validatePasswordCompliance)
    }

    private suspend fun login(clientID :String, grantType:String, agecyId:String,
                              clientSecret:String, value:String, password:String, validatePasswordCompliance:String) {
        _loginResponse.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = appRepository.login(clientID, grantType, agecyId, clientSecret, value, password, validatePasswordCompliance)
                Log.d("Response in Login ViewModel::",response.toString())
                _loginResponse.postValue(handleLoginApiResponse(response))
            } else {
                _loginResponse.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    _loginResponse.postValue(
                        Event(Resource.Error(
                            getApplication<MyApplication>().getString(
                                R.string.network_failure
                            )
                        ))
                    )
                }
                else -> {
                    _loginResponse.postValue(
                        Event(Resource.Error(
                            getApplication<MyApplication>().getString(
                                R.string.conversion_error
                            )
                        ))
                    )
                }
            }
        }

    }

    private fun handleLoginApiResponse(response: Response<LoginResponse>): Event<Resource<LoginResponse>>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Event(Resource.Success(resultResponse))
            }
        }
        return Event(Resource.Error(response.message()))
    }
}