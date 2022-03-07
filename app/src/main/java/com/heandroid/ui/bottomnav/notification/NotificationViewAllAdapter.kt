package com.heandroid.ui.bottomnav.notification

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.data.model.notification.AlertMessage
import com.heandroid.databinding.AdapterViewallNotificationBinding
import com.heandroid.listener.NotificationItemClick

class NotificationViewAllAdapter(val context: Context, private var alertMsgList: List<AlertMessage?>?) : RecyclerView.Adapter<NotificationViewAllAdapter.NotificationViewAllViewHolder>() {

    private var mListener: NotificationItemClick? = null

    fun updateHighlight(list: List<AlertMessage?>?, isHighlight: Boolean) {
        for (item in list!!) {
            item?.isSelectListItem = isHighlight
            alertMsgList = list
        }
        notifyDataSetChanged()
    }

    fun setListener(listener: NotificationItemClick) {
        mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewAllAdapter.NotificationViewAllViewHolder = NotificationViewAllViewHolder(
        AdapterViewallNotificationBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
    )

    override fun onBindViewHolder(
        holder: NotificationViewAllAdapter.NotificationViewAllViewHolder,
        position: Int
    ) {
        holder.bind(alertMsgList?.get(position))
    }

    override fun getItemCount(): Int = alertMsgList?.size ?: 0

    inner class NotificationViewAllViewHolder(var binding: AdapterViewallNotificationBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnLongClickListener {

        init {
            binding.constraintParent.setOnLongClickListener(this)
        }

        fun bind(data: AlertMessage?) {
            data?.run {
                binding.apply {
                    dateTxt.text = createTs ?: ""
                    messageTxt.text = message
                    btnTxt.visibility = View.GONE

                    if (isSelectListItem == true) {
                        binding.greenView.visibility = View.VISIBLE
                    } else {
                        binding.greenView.visibility = View.GONE
                    }
                }
            }
        }

        override fun onLongClick(view: View?): Boolean {

            binding.apply {
                if (greenView.visibility == View.VISIBLE) {
                    greenView.visibility = View.GONE
                } else {
                    greenView.visibility = View.VISIBLE
                }
            }

            return true
        }
    }
}