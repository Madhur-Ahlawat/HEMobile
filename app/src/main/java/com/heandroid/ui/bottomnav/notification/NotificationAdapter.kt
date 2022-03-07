package com.heandroid.ui.bottomnav.notification

import android.content.Context
import android.graphics.Typeface
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.data.model.notification.AlertMessage
import com.heandroid.databinding.AdapterNotificationBinding


class NotificationAdapter(private val context: Context, private val list: List<AlertMessage?>?) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.NotificationViewHolder = NotificationViewHolder(AdapterNotificationBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun getItemCount(): Int = if ((list?.size ?: 0) > 2) 2 else list?.size ?: 0

    override fun onBindViewHolder(holder: NotificationAdapter.NotificationViewHolder, position: Int) {
        holder.bind(list?.get(position))
    }

    inner class NotificationViewHolder(var binding: AdapterNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: AlertMessage?) {
            data?.run {
                binding.messageTxt.text = Html.fromHtml(message,Html.FROM_HTML_MODE_LEGACY)
                binding.messageTxt.movementMethod=LinkMovementMethod.getInstance()

                if(isViewed == "N")
                    binding.btnTxt.typeface = Typeface.DEFAULT_BOLD
                else
                    binding.btnTxt.typeface = Typeface.DEFAULT
            }
        }
    }
}
