package com.conduent.nationalhighways.data.model.pushnotification

data class PushNotificationRequest(
    val deviceToken: String,
    val osName: String,
    val osVersion: String,
    val appVersion: String,
    val language: String = "ESN",
    val optInStatus: String = "Y" //"N" for opt out
)
