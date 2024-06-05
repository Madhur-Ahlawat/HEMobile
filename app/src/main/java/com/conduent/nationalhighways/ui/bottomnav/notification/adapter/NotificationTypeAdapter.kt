package com.conduent.nationalhighways.ui.bottomnav.notification.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.notification.NotificationModel
import com.conduent.nationalhighways.listener.NotificationItemClick

class NotificationTypeAdapter(private val mContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var notificationList: List<NotificationModel> = mutableListOf()
    var mListener: NotificationItemClick? = null

    companion object {

        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_HIGH_PRIORITY = 1
        const val VIEW_TYPE_GENERAL_NOTIFICATION = 2
    }

    fun setListener(listener: NotificationItemClick) {
        mListener = listener
    }

    fun setList(list: List<NotificationModel>?) {
        if (list != null) {
            notificationList = list
        }
    }

    class HighPriorityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val dateTxt: TextView = itemView.findViewById(R.id.date_txt)
        private val msgTxt: TextView = itemView.findViewById(R.id.message_txt)
        private val btnTxt: TextView = itemView.findViewById(R.id.btn_txt)

        fun setView(
            listener: NotificationItemClick,
            context: Context,
            notificationModel: NotificationModel, position: Int
        ) {

            dateTxt.text = notificationModel.date
            msgTxt.text = notificationModel.message
            btnTxt.text = notificationModel.high_priority_btn

            if (notificationModel.isRead) {
                msgTxt.setTypeface(msgTxt.typeface, Typeface.NORMAL)
            } else {
                msgTxt.setTypeface(msgTxt.typeface, Typeface.BOLD)

            }
        }

    }

    class GeneralNotificationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val dateTxt: TextView = itemView.findViewById(R.id.date_txt)
        private val msgTxt: TextView = itemView.findViewById(R.id.message_txt)
        private val greenView: View = itemView.findViewById(R.id.green_view)

        fun setView(
            listener: NotificationItemClick,
            context: Context,
            notificationModel: NotificationModel, position: Int
        ) {

            dateTxt.text = notificationModel.date
            msgTxt.text = notificationModel.message

            itemView.setOnLongClickListener {

                listener.onLongClick(notificationModel, position)
                true
            }

            if (notificationModel.isRead) {
                msgTxt.setTypeface(msgTxt.typeface, Typeface.NORMAL)
            } else {
                msgTxt.setTypeface(msgTxt.typeface, Typeface.BOLD)

            }

            if (notificationModel.iSel) {
                greenView.visibility = View.VISIBLE
            } else {
                greenView.visibility = View.GONE

            }

        }

    }

    class NotificationHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val categoryTxt: AppCompatTextView = itemView.findViewById(R.id.category_title)
        private val viewAllTxt: AppCompatTextView = itemView.findViewById(R.id.view_all)
        private val priorityImg: AppCompatImageView = itemView.findViewById(R.id.priority_img)

        fun setView(
            listener: NotificationItemClick,
            context: Context,
            notificationModel: NotificationModel, position: Int
        ) {

            categoryTxt.text = notificationModel.category
            viewAllTxt.text = notificationModel.headerViewAll

            if (notificationModel.category == "High Priority") {
                priorityImg.visibility = View.VISIBLE
            } else {
                priorityImg.visibility = View.GONE
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        return notificationList[position].viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {

            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_notification_header, parent, false)
                return NotificationHeaderViewHolder(view)

            }

            VIEW_TYPE_GENERAL_NOTIFICATION -> {

                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_notification, parent, false)
                return GeneralNotificationHolder(view)

            }

            VIEW_TYPE_HIGH_PRIORITY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_high_priority_notifications, parent, false)
                return HighPriorityViewHolder(view)

            }

            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_high_priority_notifications, parent, false)
                return HighPriorityViewHolder(view)

            }

        }

    }

    override fun getItemCount(): Int {
        return notificationList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val model = notificationList[position]
        when (notificationList[position].viewType) {
            VIEW_TYPE_HIGH_PRIORITY -> {

                (holder as HighPriorityViewHolder).setView(mListener!!, mContext, model, position)

            }

            VIEW_TYPE_GENERAL_NOTIFICATION -> {
                (holder as GeneralNotificationHolder).setView(
                    mListener!!,
                    mContext,
                    model,
                    position
                )

            }

            VIEW_TYPE_HEADER -> {
                (holder as NotificationHeaderViewHolder).setView(
                    mListener!!,
                    mContext,
                    model,
                    position
                )

            }

        }
    }
}
