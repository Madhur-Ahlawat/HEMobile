package com.heandroid.utils.common

import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.remote.NoConnectivityException
import retrofit2.Response
import java.lang.Exception

object ResponseHandler {

    fun <T> success(response : Response<T>?,errorManager: ErrorManager): Resource<T?> {
        return if(response?.isSuccessful == true) {
            Resource.Success(response.body())
        }
        else {
            Resource.DataError(errorManager.getError(response?.code() ?: 0).description)
        }
    }

    fun <T> failure(e: Exception?) : Resource<T?> {
        if(e is NoConnectivityException)
        {
             return Resource.DataError(e.message)
        }
       return Resource.DataError(e?.message)
    }
}