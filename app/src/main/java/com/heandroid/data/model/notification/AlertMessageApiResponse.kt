package com.heandroid.data.model.notification

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AlertMessageApiResponse(

@SerializedName("statusCode") val statusCode : Int,
@SerializedName("message") val message : String,
    @SerializedName("messageList") val messageList : List<AlertMessage>
) :Serializable
