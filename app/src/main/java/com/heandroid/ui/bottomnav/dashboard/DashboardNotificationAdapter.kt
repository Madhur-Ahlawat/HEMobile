package com.heandroid.ui.bottomnav.dashboard

import android.content.Context
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.data.model.notification.AlertMessage
import com.heandroid.databinding.DashboardNotificationItemLayoutBinding

class DashboardNotificationAdapter  (private val context: Context, private val list: List<AlertMessage?>?) : RecyclerView.Adapter<DashboardNotificationAdapter.DashboardNotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardNotificationAdapter.DashboardNotificationViewHolder = DashboardNotificationViewHolder(
        DashboardNotificationItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun getItemCount(): Int = if ((list?.size ?: 0) > 2) 2 else list?.size ?: 0

    override fun onBindViewHolder(holder: DashboardNotificationAdapter.DashboardNotificationViewHolder, position: Int) {
        holder.bind(list?.get(position))
    }

    inner class DashboardNotificationViewHolder(var binding: DashboardNotificationItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: AlertMessage?) {
            data?.run {
                binding.tvTitle.text = Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY)
//                binding.btnUpdateNow.text = Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY)
                binding.btnUpdateNow.movementMethod= LinkMovementMethod.getInstance()

//                if(isViewed == "N")
//                    binding.btnTxt.typeface = Typeface.DEFAULT_BOLD
//                else
//                    binding.btnTxt.typeface = Typeface.DEFAULT
            }
        }
    }
}
