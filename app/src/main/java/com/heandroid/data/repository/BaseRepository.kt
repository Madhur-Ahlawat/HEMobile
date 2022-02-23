package com.heandroid.data.repository

import com.heandroid.data.remote.NoConnectivityException
import com.heandroid.utils.common.Resource
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

open class BaseRepository {
    suspend fun <T : Any> safeApiCall(
        call: suspend () -> Response<T>,
        errorContext: String
    ): Resource<T>? {
        val result: Resource<T> = safeApiResult(call)
        return result

    }

    private suspend fun <T : Any> safeApiResult(call: suspend () -> Response<T>): Resource<T> {
        try {
            val response = call.invoke()
            if (response.isSuccessful) return Resource.Success(response.body()!!)
            return Resource.DataError(setErrorMessage(response))
        } catch (exception: IOException) {
            if (exception is NoConnectivityException) return Resource.DataError(exception.message)
            return Resource.DataError(exception.message.toString())
        }
    }

    private fun <T : Any> setErrorMessage(response: Response<T>): String {
        val code = response.code().toString()
        val message = try {
            val jObjError = JSONObject(response.errorBody()?.string())
            jObjError.getJSONObject("error").getString("error_description")
        } catch (e: Exception) {
            e.message
        }
//        return if (message.isNullOrEmpty()) " error code = $code " else " error code = $code  & error message = $message "
        return if (message.isNullOrEmpty()) " error code = $code " else "$message "
    }


}