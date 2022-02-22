package com.heandroid.utils

import com.heandroid.data.error.errorUsecase.ErrorManager
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

object ResponseHandler {

    fun <T> success(response : Response<T>?,errorManager: ErrorManager): Resource<T?> {
        return if(response?.isSuccessful == true) { Resource.Success(response.body()) }
        else { Resource.DataError(errorManager.getError(response?.code()?:0).description) }
    }

    fun <T> failure(e: Exception?) : Resource<T?> {
       return Resource.DataError(e?.message)
    }
}