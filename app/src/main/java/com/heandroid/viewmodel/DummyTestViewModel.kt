package com.heandroid.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.model.AccountResponse
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class DummyTestViewModel(private val apiHelper: ApiHelper):ViewModel() {
    private val accountUser = MutableLiveData<Resource<Response<AccountResponse>>>()

//    init {
//        fetchAccountOverview()
//    }

     fun fetchAccountOverview(authToken:String) {
        viewModelScope.launch {
            accountUser.postValue(Resource.loading(data =null ))
            try {
                val accountOverviewFromApi = apiHelper.getAccountOverviewApiCall(authToken)
                accountUser.postValue(Resource.success(accountOverviewFromApi))
            }
            catch (e:Exception)
            {
                accountUser.postValue(Resource.error(null,e.toString() ))
            }
        }
    }

    fun getAccountOverView():LiveData<Resource<Response<AccountResponse>>> {
        return accountUser
    }
}