package com.heandroid.ui.bottomnav.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.notification.AlertMessageApiResponse
import com.heandroid.data.repository.notification.NotificationRepo
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val repo: NotificationRepo) : BaseViewModel(){

    private val alertMutData = MutableLiveData<Resource<AlertMessageApiResponse?>?>()
    val alertLivData : LiveData<Resource<AlertMessageApiResponse?>?> get() = alertMutData

    fun getAlertsApi(lang: String){
        viewModelScope.launch {
            try{
                alertMutData.postValue(ResponseHandler.success(repo.getAlertMessages(), errorManager))
            }catch (e: Exception){
                alertMutData.postValue(ResponseHandler.failure(e))
            }
        }
    }

}