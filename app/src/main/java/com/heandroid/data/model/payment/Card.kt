package com.heandroid.data.model.payment

data class Card(
    val bin: String,
    val exp: String,
    val hash: String,
    val number: String,
    val type: String
)