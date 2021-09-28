package com.heandroid.utils

import com.heandroid.model.LoginResponse
import retrofit2.Response

sealed class LoginDataState {
    data class Error(val message: String?) : LoginDataState()
    object ValidCredentialsState : LoginDataState()
    object InValidEmailState : LoginDataState()
    object InValidPasswordState : LoginDataState()
    class Success(val body: Response<LoginResponse>? = null) : LoginDataState()
}