package com.heandroid.data.model.payment

data class Check(
    val aba: String?,
    val account: String?,
    val hash: String?,
    val name: String?
)