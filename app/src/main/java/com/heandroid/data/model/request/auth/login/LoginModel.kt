package com.heandroid.data.model.request.auth.login

data class LoginModel(var value: String?="100312942",
                      var password: String?="Welcome12",
                      var enable : Boolean?=false,
                      val validatePasswordCompliance: String?="true")