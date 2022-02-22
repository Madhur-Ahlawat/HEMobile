package com.heandroid.ui.auth.login

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dummyapplication.data.model.response.LoginResponse
import com.heandroid.R
import com.heandroid.data.model.request.auth.login.LoginModel
import com.heandroid.data.model.response.auth.AuthResponseModel
import com.heandroid.data.repository.auth.LoginRepository
import com.heandroid.ui.base.BaseApplication
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.Resource
import com.heandroid.utils.ResponseHandler.failure
import com.heandroid.utils.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LoginRepository):BaseViewModel()  {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _login = MutableLiveData<Resource<LoginResponse?>?>()
    val login : LiveData<Resource<LoginResponse?>?> get()  = _login


//    /** Error handling as UI **/
//    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
//    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
//    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate
//
//    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
//    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
//    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate


    fun login(model: LoginModel?) {
        viewModelScope.launch {
            try {
                _login.postValue(success(repository.login(model),errorManager))
            } catch (e: Exception) {
                _login.postValue(failure(e))
            }
            }
        }


    fun validation(model: LoginModel?): Pair<Boolean,String> {
        var ret= Pair(true,"")
        if(model?.value?.isEmpty()==true && model.password?.isEmpty()==true) ret=Pair(false,BaseApplication.INSTANCE.getString(R.string.txt_error_username_password))
        else if(model?.value?.isEmpty()==true) ret= Pair(false,BaseApplication.INSTANCE.getString(R.string.txt_error_username))
        else if(model?.password?.isEmpty()==true) ret= Pair(false,BaseApplication.INSTANCE.getString(R.string.txt_error_password))
        return ret
    }



}