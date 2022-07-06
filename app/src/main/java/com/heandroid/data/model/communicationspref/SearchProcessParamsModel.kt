package com.heandroid.data.model.communicationspref

data class SearchProcessParamsModelReq(
    val group: String?,
    val language: String?,
    val subgroup: String?,
    val paramName: String?
)

data class SearchProcessParamsModelResp(val value: String?)
