package com.heandroid.ui.auth.forgot.password

import android.text.TextUtils
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.request.auth.forgot.password.ConfirmOptionModel
import com.heandroid.data.repository.auth.ForgotPasswordRepository
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.Resource
import com.heandroid.utils.ResponseHandler.failure
import com.heandroid.utils.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val repository: ForgotPasswordRepository): BaseViewModel() {


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _confirmOption = MutableLiveData<Resource<ConfirmOptionModel?>?>()
    val confirmOption : LiveData<Resource<ConfirmOptionModel?>?> get()  = _confirmOption


    fun confirmOptionForForgot(model: ConfirmOptionModel?){
        viewModelScope.launch {
            try {
                _confirmOption.postValue(success(repository.confirmOptionForForgot(model), errorManager))
            } catch (e: Exception) {
                _confirmOption.postValue(failure(e))
            }
        }
    }


    fun validation(model: ConfirmOptionModel?): Pair<Boolean,String> {
        var ret=Pair(true,"")
        if (TextUtils.isEmpty(model?.email) || (model?.email?.length?:0) < 3) ret=Pair(false,"Please enter email address")
        else if (TextUtils.isEmpty(model?.phone)) ret=Pair(false,"Please enter postcode")
        return ret
    }
}

