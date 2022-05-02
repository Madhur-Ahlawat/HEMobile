package com.heandroid.listener

import com.heandroid.ui.bottomnav.notification.NotificationModel


interface NotificationItemClick {


    fun onLongClick(notificationModel: NotificationModel, pos: Int)

    fun onClick(notificationModel: NotificationModel, pos: Int)
}