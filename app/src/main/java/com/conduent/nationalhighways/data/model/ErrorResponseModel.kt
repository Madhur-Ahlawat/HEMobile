package com.conduent.nationalhighways.data.model

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import retrofit2.Response

data class ErrorResponseModel(
    var error: String?,
    val exception: String?,
    @SerializedName("error_description", alternate = ["message"])
    val message: String?,
    val status: Int?,
    var errorCode: Int?,
    val timestamp: String?
) {
    companion object {
        fun parseError(response: Response<*>): ErrorResponseModel {
            val gson = Gson()
            val errorBody = response.errorBody()?.string()
            Log.e("TAG", "parseError: errorBody " + errorBody)
            return gson.fromJson(errorBody, ErrorResponseModel::class.java)
                ?: ErrorResponseModel("Unknown error", null, null, 0, 0, null)
        }
    }
}