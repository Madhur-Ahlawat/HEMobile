package com.conduent.nationalhighways.data.model.account

import com.google.gson.annotations.SerializedName

data class LRDSResponse(
    @SerializedName("srStatus") val srStatus : String?,
    @SerializedName("statusCode") var statusCode : String?,
    @SerializedName("srNumber") val srNumber : String?,
    @SerializedName("srApprovalStatus") val srApprovalStatus : String?,
    @SerializedName("message") val message : String?,
)