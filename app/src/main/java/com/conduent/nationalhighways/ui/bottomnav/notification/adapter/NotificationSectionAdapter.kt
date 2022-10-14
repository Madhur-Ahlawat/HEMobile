package com.conduent.nationalhighways.ui.bottomnav.notification.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.data.model.notification.AlertMessage
import com.conduent.nationalhighways.databinding.AdapterNotificationHeaderBinding
import com.conduent.nationalhighways.ui.bottomnav.notification.NotificationViewAllActivity


class NotificationSectionAdapter(val context: Context, private var map: MutableMap<String?, List<AlertMessage?>?>?) : RecyclerView.Adapter<NotificationSectionAdapter.NotificationHeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHeaderViewHolder = NotificationHeaderViewHolder(AdapterNotificationHeaderBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun getItemCount(): Int = map?.keys?.size ?: 0

    override fun onBindViewHolder(holder: NotificationHeaderViewHolder, position: Int) {
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
