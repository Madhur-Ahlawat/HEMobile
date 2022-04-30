package com.heandroid.data.repository.auth

import com.heandroid.data.model.auth.forgot.email.LoginModel
import com.heandroid.data.remote.ApiService
import javax.inject.Inject


class LoginRepository @Inject constructor(private val apiService: ApiService)  {

     suspend fun login(model: LoginModel?)= apiService.login(value = model?.value,
                                                             password = model?.password,
                                                             validatePasswordCompliance=model?.validatePasswordCompliance)


}