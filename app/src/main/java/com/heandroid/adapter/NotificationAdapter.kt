package com.heandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.model.NotificationModel

class NotificationAdapter(private val mContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var notificationList: List<NotificationModel> = mutableListOf()

    companion object {

        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_HIGH_PRIORITY = 1
        const val VIEW_TYPE_GENERAL_NOTIFICATION = 2
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

        fun setView(context: Context, notificationModel: NotificationModel) {

            dateTxt.text = notificationModel.date
            msgTxt.text = notificationModel.message
            btnTxt.text = notificationModel.high_priority_btn
        }

    }


    class GeneralNotificationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val dateTxt: TextView = itemView.findViewById(R.id.date_txt)
        private val msgTxt: TextView = itemView.findViewById(R.id.message_txt)

        fun setView(context: Context, notificationModel: NotificationModel) {

            dateTxt.text = notificationModel.date
            msgTxt.text = notificationModel.message
        }

    }

    class NotificationHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val categoryTxt: AppCompatTextView = itemView.findViewById(R.id.category_title)
        private val viewAllTxt: AppCompatTextView = itemView.findViewById(R.id.view_all)
        private val priorityImg: AppCompatImageView = itemView.findViewById(R.id.priority_img)

        fun setView(context: Context, notificationModel: NotificationModel) {

            categoryTxt.text = notificationModel.category
            viewAllTxt.text = notificationModel.headerViewAll
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
        return if (notificationList == null) {
            0
        } else {
            notificationList?.size
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        val model = notificationList[position]
        when (notificationList[position].viewType) {
            VIEW_TYPE_HIGH_PRIORITY -> {

                (holder as HighPriorityViewHolder).setView(mContext,model)

            }

            VIEW_TYPE_GENERAL_NOTIFICATION -> {
                (holder as GeneralNotificationHolder).setView(mContext,model)

            }

            VIEW_TYPE_HEADER -> {
                (holder as NotificationHeaderViewHolder).setView(mContext,model)

            }


        }
        val vehicleItem = notificationList[position]
//        holder.setView(mContext, vehicleItem)
    }
}
