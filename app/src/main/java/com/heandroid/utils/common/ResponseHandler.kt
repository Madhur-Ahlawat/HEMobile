package com.heandroid.utils.common

import android.util.Log
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.ErrorResponseModel
import com.heandroid.data.remote.NoConnectivityException
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

object ResponseHandler {

    fun <T> success(response : Response<T>?,errorManager: ErrorManager): Resource<T?> {
        return if(response?.isSuccessful == true) {
            Log.e("success","yes")
            Resource.Success(response.body())
        }
        else {
            val errorResponse = Gson().fromJson(response?.errorBody()?.string(),ErrorResponseModel::class.java)
            Resource.DataError(errorResponse.message)
        }
    }

    fun <T> failure(e: Exception?) : Resource<T?> {
        if(e is NoConnectivityException)
        {
             return Resource.DataError(e.message)
        }
        else if(e is SocketTimeoutException)
        {
            return Resource.DataError(Constants.VPN_ERROR)
        }
       return Resource.DataError(e?.message)
    }
}