package com.heandroid.data.model.notification

import com.google.gson.annotations.SerializedName

data class AlertMessageApiResponse(

@SerializedName("statusCode") val statusCode : Int,
@SerializedName("message") val message : String,
@SerializedName("messageList") val messageList : List<AlertMessage>
) {

}
