package com.heandroid.utils

import com.heandroid.network.RetrofitInstance

class AppRepository {

    suspend fun loginUser(body: HashMap<String, String>) =
        //RetrofitInstance.loginApi.loginUser(body)
        RetrofitInstance.loginApi.loginUser(body)


    suspend fun login(clientID:String, grantType:String,agecyId:String ,  clientSecret:String,
                      value:String,password:String ,validatePasswordCompliance:String ) =
        //RetrofitInstance.loginApi.loginUser(body)
        RetrofitInstance.loginApi.loginWithField(clientID, grantType,agecyId ,  clientSecret,
            value,password ,validatePasswordCompliance)



}