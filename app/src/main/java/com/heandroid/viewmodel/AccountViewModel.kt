package com.heandroid.viewmodel

import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.model.LogOutResp
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class AccountViewModel(private val apiHelper: ApiHelper) : ViewModel(), Observable {


    val loginOutVal = MutableLiveData<Resource<Response<LogOutResp>>>()

    fun logOutUser(){

        viewModelScope.launch {
            loginOutVal.postValue(Resource.loading(null))
        try{

            val logoutApi = apiHelper.getLogOut()

            loginOutVal.postValue(setLogOutUserResponse(logoutApi))


        }catch (e:Exception){

        }
        }

    }


    private fun setLogOutUserResponse(usersFromApi: Response<LogOutResp>): Resource<Response<LogOutResp>>? {
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


    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}