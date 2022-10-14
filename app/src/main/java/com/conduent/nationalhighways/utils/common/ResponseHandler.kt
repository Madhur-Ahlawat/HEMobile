package com.conduent.nationalhighways.utils.common

import android.text.TextUtils
import com.google.gson.Gson
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.ErrorResponseModel
import com.conduent.nationalhighways.data.remote.NoConnectivityException
import retrofit2.Response
import java.io.InterruptedIOException
import java.net.SocketTimeoutException

object ResponseHandler {

    fun <T> success(response: Response<T>?, errorManager: ErrorManager? = null): Resource<T?> {
        return if (response?.isSuccessful == true) {
            Resource.Success(response.body())
        } else {
            try {
                val errorResponse =
                    Gson().fromJson(response?.errorBody()?.string(), ErrorResponseModel::class.java)
                if (TextUtils.isEmpty(errorResponse.message)) {
                    return Resource.DataError(errorResponse.exception, errorResponse)
                }
                return Resource.DataError(errorResponse.message, errorResponse)
            } catch (e: Exception) {
                return Resource.DataError(e.message)
            }
        }
    }

    fun <T> failure(e: Exception?): Resource<T?> {
        if (e is NoConnectivityException) {
            return Resource.DataError(e.message)
        } else if (e is SocketTimeoutException || e is InterruptedIOException) {
            return Resource.DataError(Constants.VPN_ERROR)
        }
        return Resource.DataError(e?.message)
    }
}