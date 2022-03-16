package com.heandroid.ui.account.creation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.account.CreateAccountResponseModel
import com.heandroid.data.repository.auth.CreateAccountRespository
import com.heandroid.data.repository.auth.LoginRepository
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler.failure
import com.heandroid.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateAccountViewModel @Inject constructor(private val repository: CreateAccountRespository): BaseViewModel()  {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _createAccount = MutableLiveData<Resource<CreateAccountResponseModel?>?>()
    val createAccount : LiveData<Resource<CreateAccountResponseModel?>?> get()  = _createAccount


    fun createAccount(model: CreateAccountRequestModel?) {
        viewModelScope.launch {
            try {
                _createAccount.postValue(success(repository.createAccount(model), errorManager))
            } catch (e: Exception) {
                _createAccount.postValue(failure(e))
            }
        }
    }

}