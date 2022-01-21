package com.heandroid.listener

import com.heandroid.model.NotificationModel

interface NotificationItemClick {


    fun onLongClick(notificationModel: NotificationModel, pos: Int)

    fun onClick(notificationModel: NotificationModel, pos: Int)

}