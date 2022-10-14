package com.conduent.nationalhighways.data.model

import com.google.gson.annotations.SerializedName

data class ErrorResponseModel(
    val error: String?,
    val exception: String?,

    @SerializedName("error_description", alternate = ["message"])
    val message: String?,
    val status: Int?,
    val errorCode: Int?,
    val timestamp: String?
)