package com.conduent.nationalhighways.utils.common

import android.text.TextUtils
import android.util.Log
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.ErrorResponseModel
import com.conduent.nationalhighways.data.remote.NoConnectivityException
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Response
import java.io.InterruptedIOException
import java.net.SocketTimeoutException

object ResponseHandler {

    fun <T> success(response: Response<T>?, errorManager: ErrorManager? = null): Resource<T?> {

        Log.e("TAG", "success: response code " + response?.code())
        Log.e("TAG", "success: response isSuccessful " + response?.isSuccessful)

        return if (response?.isSuccessful == true) {
            if (response.code() == Constants.TOKEN_FAIL) {
                return Resource.DataError(
                    "Token Expired",
                    ErrorResponseModel(Constants.INVALID_TOKEN, "", Constants.INVALID_TOKEN, 401, 401, "")
                )
            } else {
                Resource.Success(response.body())
            }
        } else {
            try {
                val errorResponse =
                    parseError(response)
                Log.e("TAG", "success: response error " + errorResponse?.error.equals(Constants.INVALID_TOKEN))
                Log.e("TAG", "success: response error--> " + errorResponse?.error)

                if (response?.code() == Constants.TOKEN_FAIL && errorResponse?.error.equals(Constants.INVALID_TOKEN)) {
                    return Resource.DataError(
                        "Token Expired",
                        ErrorResponseModel(Constants.INVALID_TOKEN, "", Constants.INVALID_TOKEN, 401, 401, "")
                    )
                } else {
                    if (TextUtils.isEmpty(errorResponse?.message)) {
                        return Resource.DataError(errorResponse?.exception, errorResponse)
                    }
                    return Resource.DataError(errorResponse?.message, errorResponse)
                }
            } catch (e: Exception) {
                Log.e("TAG", "success: message "+e.message )
                return Resource.DataError(e.message)
            }
        }
    }

    private fun <T> parseError(response: Response<T>?): ErrorResponseModel? {
        val gson = Gson()
        val errorBody = response?.errorBody()?.string()
//        Log.e("TAG", "parseError: errorBody $errorBody")

        return try {
            Log.e("TAG", "parseError: message try -> " )
            gson.fromJson(errorBody, ErrorResponseModel::class.java)
                ?: ErrorResponseModel(Constants.INVALID_TOKEN, null, Constants.INVALID_TOKEN, 0, 0, null)
        } catch (e: JsonSyntaxException) {
            Log.e("TAG", "parseError: message -> " +e.message)
            ErrorResponseModel(Constants.INVALID_TOKEN, null, errorBody, 0, 0, null)
        }
    }




    fun <T> failure(e: Exception?): Resource<T?> {
        Log.e("TAG", "failure: e " + e)
        if (e is NoConnectivityException) {
            return Resource.DataError(e.message)
        } else if (e is SocketTimeoutException) {
            return Resource.DataError(
                Constants.VPN_ERROR,
                ErrorResponseModel(
                    Constants.API_TIME_OUT,
                    "",
                    "",
                    0,
                    Constants.API_TIMEOUT_ERROR,
                    ""
                )
            )
        } else if (e is InterruptedIOException) {
            return Resource.DataError(Constants.VPN_ERROR)
        }
        return Resource.DataError(e?.message)
    }
}