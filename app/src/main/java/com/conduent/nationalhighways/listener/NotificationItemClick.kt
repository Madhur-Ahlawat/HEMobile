package com.conduent.nationalhighways.listener

import com.conduent.nationalhighways.data.model.notification.NotificationModel


interface NotificationItemClick {


    fun onLongClick(notificationModel: NotificationModel, pos: Int)

    fun onClick(notificationModel: NotificationModel, pos: Int)
}