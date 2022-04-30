package com.heandroid.ui.bottomnav.notification

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.data.model.notification.AlertMessage
import com.heandroid.databinding.AdapterNotificationHeaderBinding
import com.heandroid.ui.auth.controller.AuthActivity


class NotificationSectionAdapter(val context: Context, private var map: MutableMap<String?, List<AlertMessage?>?>?) : RecyclerView.Adapter<NotificationSectionAdapter.NotificationHeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationSectionAdapter.NotificationHeaderViewHolder = NotificationHeaderViewHolder(AdapterNotificationHeaderBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun getItemCount(): Int = map?.keys?.size ?: 0

    override fun onBindViewHolder(holder: NotificationSectionAdapter.NotificationHeaderViewHolder, position: Int) {
        val keyName = map?.keys?.toMutableList()?.get(position)
        holder.bind(keyName, map?.get(keyName))
    }

    inner class NotificationHeaderViewHolder(var binding: AdapterNotificationHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //todo remove hard coded string and use either from constants or string.xml file
        fun bind(title: String?, data: List<AlertMessage?>?) {
            data?.run {
                binding.categoryTitle.text = title
                binding.viewAll.setOnClickListener {
                    context.startActivity(
                        Intent(context, NotificationViewAllActivity::class.java)
                            .putExtra("categoryName", title)
                            .putParcelableArrayListExtra("list", ArrayList(data)))
                }
                binding.rvNotifications.layoutManager = LinearLayoutManager(context)
                binding.rvNotifications.adapter = NotificationAdapter(context, data)
            }
        }
    }
}
